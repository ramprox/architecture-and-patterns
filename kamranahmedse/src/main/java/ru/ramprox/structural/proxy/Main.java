package ru.ramprox.structural.proxy;

import ru.ramprox.structural.proxy.door.LabDoor;
import ru.ramprox.structural.proxy.door.SecuredDoor;

public class Main {
    public static void main(String[] args) {
        SecuredDoor door = new SecuredDoor(new LabDoor());
        door.open("invalid");
        door.open("$ecr@t");
        door.close();
    }
}
