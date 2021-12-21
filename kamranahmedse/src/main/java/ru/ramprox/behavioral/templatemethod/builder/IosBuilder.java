package ru.ramprox.behavioral.templatemethod.builder;

public class IosBuilder extends Builder {
    @Override
    protected void test() {
        System.out.println("Running ios tests");
    }

    @Override
    protected void lint() {
        System.out.println("Linting the ios code");
    }

    @Override
    protected void assemble() {
        System.out.println("Assembling the ios build");
    }

    @Override
    protected void deploy() {
        System.out.println("Deploying ios build to server");
    }
}
