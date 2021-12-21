package ru.ramprox.server.factory.model;

import java.lang.reflect.Constructor;

public class ObjectCreatingInfo<T> {
    private String name;
    private Class<T> impl;
    private Constructor<T> ctor;
    private Class<?>[] ctorArgsClasses;
    private Class<T> interfaceClass;

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<T> getImpl() {
        return impl;
    }

    public void setImpl(Class<T> impl) {
        this.impl = impl;
    }

    public Constructor<T> getCtor() {
        return ctor;
    }

    public void setCtor(Constructor<T> ctor) {
        this.ctor = ctor;
        if(ctor != null) {
            ctorArgsClasses = new Class<?>[this.ctor.getParameterTypes().length];
        }
    }

    public Class<?>[] getCtorArgsClasses() {
        return ctorArgsClasses;
    }

    public void setCtorArgsClasses(Class<?>[] ctorArgsClasses) {
        this.ctorArgsClasses = ctorArgsClasses;
    }

    public <T1> void setCtorArgClass(int index, Class<T1> ctorArgClass) {
        this.ctorArgsClasses[index] = ctorArgClass;
    }
}
