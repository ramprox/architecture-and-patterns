package ru.ramprox.server.factory.context;

import ru.ramprox.server.factory.Factory;

public interface Context {
    <T> T get(Class<T> objectClass);
    void setFactory(Factory factory);
    <T> T get(Class<T> objectClass, String name);
}