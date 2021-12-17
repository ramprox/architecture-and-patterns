package ru.ramprox.structural.decorator;

import ru.ramprox.structural.decorator.milk.*;

public class Main {
    public static void main(String[] args) {
        Coffee someCoffee = new SimpleCoffee();
        System.out.println(someCoffee.getCost());
        System.out.println(someCoffee.getDescription());

        someCoffee = new MilkCoffee(someCoffee);
        System.out.println(someCoffee.getCost());
        System.out.println(someCoffee.getDescription());

        someCoffee = new WhipCoffee(someCoffee);
        System.out.println(someCoffee.getCost());
        System.out.println(someCoffee.getDescription());

        someCoffee = new VanillaCoffee(someCoffee);
        System.out.println(someCoffee.getCost());
        System.out.println(someCoffee.getDescription());
    }
}
