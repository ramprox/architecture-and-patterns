package ru.ramprox.server.factory;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Order;
import ru.ramprox.server.annotation.Value;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.factory.context.Context;
import ru.ramprox.server.factory.environment.Environment;
import ru.ramprox.server.factory.model.ObjectCreatingInfo;
import ru.ramprox.server.factory.objectconfigurer.ObjectConfigurer;
import ru.ramprox.server.factory.objectinfoconfigurer.ObjectInfoConfigurer;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Factory {

    private final Context context;
    private final Reflections scanner;
    private final List<ObjectInfoConfigurer> objectInfoConfigurers = new LinkedList<>();
    private final List<ObjectConfigurer> objectConfigurers = new LinkedList<>();

    private final Map<String, ObjectCreatingInfo<?>> creatingInfoMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(Factory.class);

    public Factory(Context context) {
        this.context = context;
        String packagesToScan = context.get(Environment.class).getProperty(PropertyName.PACKAGES_TO_SCAN);
        this.scanner = new Reflections("ru.ramprox.server.factory", packagesToScan);
        createConfigurerObjects();
        scanPackages();
    }

    private void createConfigurerObjects() {
        scanner.getSubTypesOf(ObjectConfigurer.class)
                .stream()
                .map(Utils::createObject)
                .forEach(objectConfigurers::add);
        scanner.getSubTypesOf(ObjectInfoConfigurer.class)
                .stream()
                .sorted(Comparator.comparingInt(aClass -> aClass.getAnnotation(Order.class).value()))
                .map(Utils::createObject)
                .forEach(objectInfoConfigurers::add);
    }

    public Map<String, ObjectCreatingInfo<?>> getCreatingInfoMap() {
        return Collections.unmodifiableMap(creatingInfoMap);
    }

    private void scanPackages() {
        Set<Class<?>> classesAnnotatedWithComponent = scanner.getTypesAnnotatedWith(Component.class);
        classesAnnotatedWithComponent.forEach(this::putCreatingInfo);
        objectInfoConfigurers.forEach(configurer -> configurer.infoConfigure(this));
    }

    private <T> void putCreatingInfo(Class<T> classAnnotatedWithComponent) {
        ObjectCreatingInfo<T> creatingInfo = new ObjectCreatingInfo<>();
        Component component = classAnnotatedWithComponent.getAnnotation(Component.class);
        creatingInfo.setName(!component.name().isEmpty() ? component.name() : classAnnotatedWithComponent.getSimpleName());
        creatingInfo.setImpl(classAnnotatedWithComponent);
        creatingInfo.setInterfaceClass((Class<T>) classAnnotatedWithComponent.getInterfaces()[0]);
        creatingInfoMap.put(creatingInfo.getName(), creatingInfo);
    }

    @SuppressWarnings("unchecked")
    public <T> T createObject(Class<T> objectClass) {
        ObjectCreatingInfo<?> creatingInfo;
        if(objectClass.isInterface()) {
            creatingInfo = getInfo(objectClass);
        } else {
            creatingInfo = getInfoByImpl(objectClass).get();
        }
        Object[] injectingObjects = getInjectingObjects(creatingInfo);
        T result;
        result = (T)createObject(creatingInfo.getImpl(), injectingObjects);
        objectConfigurers.forEach(configurer -> configurer.configure(result, context));
        callPostConstruct(result);
        return result;
    }

    private void callPostConstruct(Object object) {
        Optional<Method> postConstructMethod = Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .findFirst();

        postConstructMethod.ifPresent(postConstruct -> {
            postConstruct.setAccessible(true);
            try {
                postConstruct.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error invoke postconsruct method {}: {}",
                        postConstruct.getName(), e.getMessage());
            }
        });
    }

    private <T> Optional<ObjectCreatingInfo<?>> getInfoByImpl(Class<T> impl) {
        return creatingInfoMap.values().stream()
                .filter(info -> info.getImpl().equals(impl))
                .findFirst();
    }

    private <T> ObjectCreatingInfo<?> getInfo(Class<T> ifc) {
        Set<ObjectCreatingInfo<?>> collect = creatingInfoMap.values().stream()
                .filter(info -> info.getInterfaceClass().equals(ifc))
                .collect(Collectors.toSet());
        if(collect.size() == 0) {
            logger.error("No impl of class: {}", ifc.getName());
            throw new RuntimeException("No impl of class: " + ifc.getName());
        }
        if(collect.size() > 1) {
            logger.error("Multiple impl of class: {}", ifc.getName());
            throw new RuntimeException("Multiple impl of class");
        }
        return collect.iterator().next();
    }

    private <T> Object[] getInjectingObjects(ObjectCreatingInfo<T> creatingInfo) {
        Constructor<T> injectingCtor = creatingInfo.getCtor();
        if(injectingCtor != null) {
            Class<?>[] parameterTypes = creatingInfo.getCtorArgsClasses();
            Object[] result = new Object[parameterTypes.length];
            for(int i = 0; i < result.length; i++) {
                result[i] = context.get(parameterTypes[i]);
            }
            return result;
        }
        return new Object[0];
    }

    public <T> T createObject(Class<T> objectClass, Object... ctorArgs) {
        Class<?>[] argClasses = new Class<?>[ctorArgs.length];
        for(int i = 0; i < argClasses.length; i++) {
            argClasses[i] = ctorArgs[i].getClass();
        }
        try {
            Constructor<T> ctor = findCompatibleConstructor(objectClass, argClasses);
            if(ctor != null) {
                ctor.setAccessible(true);
                if(ctorArgs.length == 0) {
                    return ctor.newInstance();
                }
                return objectClass.cast(ctor.newInstance(ctorArgs));
            } else {
                throw new NoSuchMethodException("Constructor not found");
            }
        } catch (InstantiationException e) {
            logger.error("Can't initialize class " + objectClass.getName() + ": {}", e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("Can't assess to constructor of class " + objectClass.getName() + ": {}", e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("Can't invoke constructor of class " + objectClass.getName() + ": {}", e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error("Constructor of class " + objectClass.getName() + " not found: {}", e.getMessage());
        }
        return null;
    }

    private <T> Constructor<T> findCompatibleConstructor(Class<T> objectClass, Class<?>... ctorArgTypes) {
        Constructor<?>[] ctors = objectClass.getDeclaredConstructors();
        for(Constructor<?> ctor : ctors) {
            Class<?>[] argTypes = ctor.getParameterTypes();
            if(isAssignable(argTypes, ctorArgTypes)) {
                return (Constructor<T>) ctor;
            }
        }
        return null;
    }

    private boolean isAssignable(Class<?>[] argTypes, Class<?>[] ctorArgTypes) {
        for(int i = 0; i < argTypes.length; i++) {
            if(!argTypes[i].isAssignableFrom(ctorArgTypes[i])) {
                return false;
            }
        }
        return true;
    }
}