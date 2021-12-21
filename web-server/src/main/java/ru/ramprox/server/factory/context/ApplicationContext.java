package ru.ramprox.server.factory.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.factory.environment.Environment;
import ru.ramprox.server.factory.Factory;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext implements Context {

    private Factory factory;

    private final Map<String, Object> objects = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    public ApplicationContext(Environment environment) {
        objects.put(Environment.class.getSimpleName(), environment);
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> objectClass) {
        T object = (T)objects.get(objectClass.getSimpleName());
        if(object == null) {
            try {
                object = factory.createObject(objectClass);
                putInMemory(objectClass, object);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> objectClass, String name) {
        T object = (T)objects.get(name);
        if(object == null) {
            object = factory.createObject(objectClass);
            putInMemory(objectClass, object);
        }
        return object;
    }

    private <T> void putInMemory(Class<T> objectClass, Object object) {
        boolean isAnnotationPresent = object.getClass().isAnnotationPresent(Component.class);
        if(isAnnotationPresent) {
            Component annotation = object.getClass().getAnnotation(Component.class);
            String name = !annotation.name().isEmpty() ? annotation.name() : objectClass.getSimpleName();
            objects.put(name, object);
        } else {
            objects.put(objectClass.getSimpleName(), object);
        }
    }
}