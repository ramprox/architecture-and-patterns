package ru.ramprox.behavioral.visitor.operation;

import ru.ramprox.behavioral.visitor.animal.Dolphin;
import ru.ramprox.behavioral.visitor.animal.Lion;
import ru.ramprox.behavioral.visitor.animal.Monkey;

public class Speak implements AnimalOperation {
    @Override
    public void visitMonkey(Monkey monkey) {
        monkey.shout();
    }

    @Override
    public void visitLion(Lion lion) {
        lion.roar();
    }

    @Override
    public void visitDolphin(Dolphin dolphin) {
        dolphin.speak();
    }
}
