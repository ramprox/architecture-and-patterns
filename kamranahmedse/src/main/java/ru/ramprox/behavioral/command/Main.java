package ru.ramprox.behavioral.command;

import ru.ramprox.behavioral.command.command.Command;
import ru.ramprox.behavioral.command.command.TurnOff;
import ru.ramprox.behavioral.command.command.TurnOn;

public class Main {
    public static void main(String[] args) {
        Bulb bulb = new Bulb();
        Command turnOn = new TurnOn(bulb);
        Command turnOff = new TurnOff(bulb);
        RemoteControl remote = new RemoteControl();
        remote.submit(turnOn);
        remote.submit(turnOff);
    }
}
