package ru.ramprox.server.handler;

import org.reflections.Reflections;
import ru.ramprox.server.annotation.Handler;
import ru.ramprox.server.service.interfaces.ContentTypeResolver;
import ru.ramprox.server.service.interfaces.ResourceReader;
import ru.ramprox.server.service.interfaces.ResourceResolver;
import ru.ramprox.server.service.simpleserviceimpl.ServiceFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class HandlerFactory {

    private final ServiceFactory serviceFactory;
    private final Map<Class<?>, Object> objects;
    private final Reflections scanner;

    public HandlerFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.scanner = new Reflections("ru.ramprox");
        objects = initialize();
    }

    protected Map<Class<?>, Object> initialize() {

        Set<Class<?>> classesWithAnnotationHandler = scanner.getTypesAnnotatedWith(Handler.class);
        List<Class<?>> sortedclassesWithAnnotationHandler = classesWithAnnotationHandler.stream()
                .sorted((aClass1, aClass2) -> aClass2.getAnnotation(Handler.class).order() - aClass1.getAnnotation(Handler.class).order())
                .collect(Collectors.toList());

        List<RequestHandler> handlers = new ArrayList<>();

        sortedclassesWithAnnotationHandler.forEach(classHandler -> {
            Constructor<?> constructor = classHandler.getDeclaredConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] ctorObjects = new Object[parameterTypes.length];
            for(int i = 0; i < ctorObjects.length; i++) {
                if(parameterTypes[i].getName().contains("service")) {
                    ctorObjects[i] = serviceFactory.get(parameterTypes[i]);
                } else if(parameterTypes[i].getName().contains("handler")) {
                    if(handlers.size() > 0) {
                        ctorObjects[i] = handlers.get(handlers.size() - 1);
                    }
                }
            }
            try {
                handlers.add((RequestHandler) constructor.newInstance(ctorObjects));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        Map<Class<?>, Object> result = new HashMap<>();
        result.put(RequestHandler.class, handlers.get(handlers.size() - 1));

        ExceptionHandler exceptionHandler = new ExceptionHandlerImpl(serviceFactory.get(ResourceResolver.class),
                serviceFactory.get(ContentTypeResolver.class),
                serviceFactory.get(ResourceReader.class));
        result.put(ExceptionHandler.class, exceptionHandler);
        return result;
    }

    public <T> T get(Class<T> cl) {
        return cl.cast(objects.get(cl));
    }
}
