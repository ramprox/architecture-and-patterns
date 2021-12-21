package ru.ramprox.server.factory.objectinfoconfigurer;

import ru.ramprox.server.annotation.Handler;
import ru.ramprox.server.annotation.Order;
import ru.ramprox.server.factory.Factory;
import ru.ramprox.server.handler.RequestHandler;
import ru.ramprox.server.factory.model.ObjectCreatingInfo;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Order(2)
public class HandlerObjectInfoConfigurer implements ObjectInfoConfigurer {
    @Override
    public void infoConfigure(Factory factory) {
        Map<String, ObjectCreatingInfo<?>> creatingInfoMap = factory.getCreatingInfoMap();
        creatingInfoMap.values()
                .forEach(creatingInfo -> this.configure(creatingInfo, factory));
    }

    private <T> void configure(ObjectCreatingInfo<T> creatingInfo, Factory factory) {
        if(creatingInfo.getCtor() == null) {
            return;
        }
        Constructor<T> constructor = creatingInfo.getCtor();
        Optional<Class<?>> aClass = Arrays.stream(constructor.getParameterTypes())
                .filter(parameterType -> parameterType.equals(RequestHandler.class))
                .findFirst();
        if(aClass.isPresent()) {
            Handler handler = creatingInfo.getImpl().getAnnotation(Handler.class);
            Optional<ObjectCreatingInfo<T>> optInfo = handler != null ?
                    getPrevious(factory.getCreatingInfoMap(), handler.order())
                    : getFirst(factory.getCreatingInfoMap());
            optInfo.ifPresent(info -> setConstructorArgs(creatingInfo, constructor, info));
        }
    }

    private <T> void setConstructorArgs(ObjectCreatingInfo<T> creatingInfo, Constructor<T> constructor, ObjectCreatingInfo<T> info) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(RequestHandler.class)) {
                creatingInfo.getCtorArgsClasses()[i] = info.getImpl();
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<ObjectCreatingInfo<T>> getPrevious(Map<String, ObjectCreatingInfo<?>> map, int order) {
        return map.values().stream()
                .filter(configureInfo -> configureInfo.getImpl().isAnnotationPresent(Handler.class))
                .sorted(Comparator.comparingInt(configureInfo -> configureInfo.getImpl().getAnnotation(Handler.class).order()))
                .filter(configureInfo -> configureInfo.getImpl().getAnnotation(Handler.class).order() > order)
                .findFirst()
                .map(objectCreatingInfo -> (ObjectCreatingInfo<T>)objectCreatingInfo);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<ObjectCreatingInfo<T>> getFirst(Map<String, ObjectCreatingInfo<?>> map) {
        return map.values().stream()
                .filter(configureInfo -> configureInfo.getImpl().isAnnotationPresent(Handler.class))
                .sorted(Comparator.comparingInt(configureInfo -> configureInfo.getImpl().getAnnotation(Handler.class).order()))
                .findFirst()
                .map(objectCreatingInfo -> (ObjectCreatingInfo<T>)objectCreatingInfo);
    }
}
