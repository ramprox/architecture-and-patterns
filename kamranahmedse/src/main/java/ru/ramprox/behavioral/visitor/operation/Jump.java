package ru.ramprox.behavioral.visitor.operation;

import ru.ramprox.behavioral.visitor.animal.Dolphin;
import ru.ramprox.behavioral.visitor.animal.Lion;
import ru.ramprox.behavioral.visitor.animal.Monkey;

public class Jump implements AnimalOperation {
    @Override
    public void visitMonkey(Monkey monkey) {
        System.out.println("Jumped 20 feet high! on to the tree!");
    }

    @Override
    public void visitLion(Lion lion) {
        System.out.println("Jumped 7 feet! Back on the ground!");
    }

    @Override
    public void visitDolphin(Dolphin dolphin) {
        System.out.println("Walked on water a little and disappeared!");
    }
}
