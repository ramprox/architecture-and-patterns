package ru.ramprox.server.factory.contextconfig;

import ru.ramprox.server.factory.environment.Environment;
import ru.ramprox.server.factory.context.Context;

public interface ContextConfig {
    Context getContext(Environment environment);
    Context getContext(String... args);
}