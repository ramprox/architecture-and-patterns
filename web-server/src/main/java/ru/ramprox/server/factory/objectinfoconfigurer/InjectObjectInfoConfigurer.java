package ru.ramprox.server.factory.objectinfoconfigurer;

import ru.ramprox.server.annotation.Inject;
import ru.ramprox.server.annotation.Order;
import ru.ramprox.server.factory.Factory;
import ru.ramprox.server.factory.model.ObjectCreatingInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Order(1)
public class InjectObjectInfoConfigurer implements ObjectInfoConfigurer {

    @Override
    public void infoConfigure(Factory factory) {
        Map<String, ObjectCreatingInfo<?>> creatingInfoMap = factory.getCreatingInfoMap();
        creatingInfoMap.values().forEach(creatingInfo -> setProperty(creatingInfo, factory));
    }

    private <T> void setProperty(ObjectCreatingInfo<T> creatingInfo, Factory factory) {
        Constructor<T> injectingConstructor = getInjectingConstructor(creatingInfo.getImpl());
        creatingInfo.setCtor(injectingConstructor);
        creatingInfo.setCtorArgsClasses(getInjectingCtorArgs(injectingConstructor, factory));
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getInjectingConstructor(Class<T> objectClass) {
        Optional<Constructor<?>> optConst = Arrays.stream(objectClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .findFirst();
        return (Constructor<T>)optConst.orElse(null);
    }

    private <T> Class<?>[] getInjectingCtorArgs(Constructor<T> injectingCtor, Factory factory) {
        if(injectingCtor != null) {
            Class<?>[] parameterTypes = injectingCtor.getParameterTypes();
            Class<?>[] result = new Class<?>[parameterTypes.length];
            Annotation[][] parameterAnnotations = injectingCtor.getParameterAnnotations();
            for(int i = 0; i < result.length; i++) {
                Annotation injectAnno = parameterAnnotations[i].length > 0 ? parameterAnnotations[i][0] : null;
                if(injectAnno instanceof Inject) {
                    Inject inject = (Inject)injectAnno;
                    result[i] = getImplClass(factory.getCreatingInfoMap(), inject.name());
                } else {
                    result[i] = getImplClass(factory.getCreatingInfoMap(), parameterTypes[i]);
                }
            }
            return result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> getImplClass(Map<String, ObjectCreatingInfo<?>> map, Class<T> ifc) {
        return map.values().stream()
                .filter(creatingInfo -> creatingInfo.getInterfaceClass().equals(ifc))
                .map(creatingInfo -> (Class<? extends T>)creatingInfo.getImpl())
                .findFirst().orElseGet(null);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> getImplClass(Map<String, ObjectCreatingInfo<?>> map, String name) {
        return map.values().stream()
                .filter(creatingInfo -> creatingInfo.getName().equals(name))
                .map(creatingInfo -> (Class<? extends T>)creatingInfo.getImpl())
                .findFirst().orElseGet(null);
    }
}
