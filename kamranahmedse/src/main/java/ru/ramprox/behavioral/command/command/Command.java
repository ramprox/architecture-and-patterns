package ru.ramprox.behavioral.command.command;

public interface Command {
    void execute();
    void undo();
    void redo();
}
