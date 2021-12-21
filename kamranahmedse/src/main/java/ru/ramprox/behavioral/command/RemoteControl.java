package ru.ramprox.behavioral.command;

import ru.ramprox.behavioral.command.command.Command;

public class RemoteControl {
    public void submit(Command command) {
        command.execute();
    }
}
