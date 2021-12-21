package ru.ramprox.behavioral.visitor.operation;

import ru.ramprox.behavioral.visitor.animal.Dolphin;
import ru.ramprox.behavioral.visitor.animal.Lion;
import ru.ramprox.behavioral.visitor.animal.Monkey;

public interface AnimalOperation {
    void visitMonkey(Monkey monkey);
    void visitLion(Lion lion);
    void visitDolphin(Dolphin dolphin);
}
