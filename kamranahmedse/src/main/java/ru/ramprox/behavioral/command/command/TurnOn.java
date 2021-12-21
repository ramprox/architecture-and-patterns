package ru.ramprox.behavioral.command.command;

import ru.ramprox.behavioral.command.Bulb;

public class TurnOn implements Command {

    private Bulb bulb;

    public TurnOn(Bulb bulb) {
        this.bulb = bulb;
    }

    @Override
    public void execute() {
        bulb.turnOn();
    }

    @Override
    public void undo() {
        bulb.turnOff();
    }

    @Override
    public void redo() {
        execute();
    }
}
