package ru.ramprox.behavioral.visitor.animal;

import ru.ramprox.behavioral.visitor.operation.AnimalOperation;

public interface Animal {
    void accept(AnimalOperation operation);
}
