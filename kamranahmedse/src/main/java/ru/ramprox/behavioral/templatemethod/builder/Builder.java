package ru.ramprox.behavioral.templatemethod.builder;

public abstract class Builder {
    public final void build() {
        test();
        lint();
        assemble();
        deploy();
    }

    protected abstract void test();
    protected abstract void lint();
    protected abstract void assemble();
    protected abstract void deploy();
}
