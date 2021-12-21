package ru.ramprox.behavioral.visitor.animal;

import ru.ramprox.behavioral.visitor.operation.AnimalOperation;

public class Monkey implements Animal {

    public void shout() {
        System.out.println("Ooh oo aa aa!");
    }

    @Override
    public void accept(AnimalOperation operation) {
        operation.visitMonkey(this);
    }
}
