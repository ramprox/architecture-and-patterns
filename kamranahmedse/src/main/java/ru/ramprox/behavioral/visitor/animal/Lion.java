package ru.ramprox.behavioral.visitor.animal;

import ru.ramprox.behavioral.visitor.operation.AnimalOperation;

public class Lion implements Animal {

    public void roar() {
        System.out.println("Roaaar!");
    }

    @Override
    public void accept(AnimalOperation operation) {
        operation.visitLion(this);
    }
}
