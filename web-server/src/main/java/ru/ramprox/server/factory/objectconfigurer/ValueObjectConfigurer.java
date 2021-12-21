package ru.ramprox.server.factory.objectconfigurer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Value;
import ru.ramprox.server.factory.PropertyToCollectionConverter;
import ru.ramprox.server.factory.environment.Environment;
import ru.ramprox.server.factory.context.Context;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ValueObjectConfigurer implements ObjectConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ValueObjectConfigurer.class);

    private static final PropertyToCollectionConverter converter = new PropertyToCollectionConverter();

    @Override
    public void configure(Object object, Context context) {
        Field[] fields = object.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (field.isAnnotationPresent(Value.class)) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                Environment environment = context.get(Environment.class);
                field.setAccessible(true);
                try {
                    String property = !valueAnnotation.name().isEmpty() ? environment.getProperty(valueAnnotation.name()) : "";
                    String value = property != null ? property : valueAnnotation.defaultValue();
                    Class<?> type = field.getType();
                    if(List.class.isAssignableFrom(type)) {
                        field.set(object, converter.convertToList(value));
                    } else if(Set.class.isAssignableFrom(type)) {
                        field.set(object, converter.convertToSet(value));
                    } else {
                        field.set(object, value);
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Can't access to field {}: {}", field.getName(), e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
