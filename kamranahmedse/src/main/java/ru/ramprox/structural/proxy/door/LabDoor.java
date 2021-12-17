package ru.ramprox.structural.proxy.door;

public class LabDoor implements Door {
    @Override
    public void open() {
        System.out.println("Opening lab door");
    }

    @Override
    public void close() {
        System.out.println("Closing lab door");
    }
}
