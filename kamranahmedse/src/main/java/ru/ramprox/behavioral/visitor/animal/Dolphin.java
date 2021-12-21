package ru.ramprox.behavioral.visitor.animal;

import ru.ramprox.behavioral.visitor.operation.AnimalOperation;

public class Dolphin implements Animal {

    public void speak() {
        System.out.println("Tuut tuttu tuutt!");
    }

    @Override
    public void accept(AnimalOperation operation) {
        operation.visitDolphin(this);
    }
}
