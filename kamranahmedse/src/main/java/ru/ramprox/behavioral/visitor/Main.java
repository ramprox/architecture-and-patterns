package ru.ramprox.behavioral.visitor;

import ru.ramprox.behavioral.visitor.animal.Animal;
import ru.ramprox.behavioral.visitor.animal.Dolphin;
import ru.ramprox.behavioral.visitor.animal.Lion;
import ru.ramprox.behavioral.visitor.animal.Monkey;
import ru.ramprox.behavioral.visitor.operation.AnimalOperation;
import ru.ramprox.behavioral.visitor.operation.Jump;
import ru.ramprox.behavioral.visitor.operation.Speak;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<Animal> animals = Arrays.asList(new Monkey(), new Lion(), new Dolphin());

        AnimalOperation speak = new Speak();
        animals.forEach(animal -> animal.accept(speak));

        AnimalOperation jump = new Jump();

        animals.forEach(animal -> animal.accept(jump));
    }
}
