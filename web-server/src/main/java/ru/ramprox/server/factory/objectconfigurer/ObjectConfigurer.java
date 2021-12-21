package ru.ramprox.server.factory.objectconfigurer;

import ru.ramprox.server.factory.context.Context;

public interface ObjectConfigurer {
    void configure(Object object, Context context);
}
