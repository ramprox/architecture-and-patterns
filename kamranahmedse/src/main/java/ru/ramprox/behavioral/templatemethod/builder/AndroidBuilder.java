package ru.ramprox.behavioral.templatemethod.builder;

public class AndroidBuilder extends Builder {
    @Override
    protected void test() {
        System.out.println("Running android tests");
    }

    @Override
    protected void lint() {
        System.out.println("Linting the android code");
    }

    @Override
    protected void assemble() {
        System.out.println("Assembling the android build");
    }

    @Override
    protected void deploy() {
        System.out.println("Deploying android build to server");
    }
}
