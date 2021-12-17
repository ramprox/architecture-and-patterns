package ru.ramprox.structural.facade;

import ru.ramprox.structural.facade.computer.Computer;
import ru.ramprox.structural.facade.computer.ComputerFacade;

public class Main {
    public static void main(String[] args) {
        ComputerFacade computerFacade = new ComputerFacade(new Computer());
        computerFacade.turnOn();
        System.out.println();
        computerFacade.turnOff();
    }
}
