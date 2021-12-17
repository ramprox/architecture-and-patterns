package ru.ramprox.structural.decorator.milk;

public class SimpleCoffee implements Coffee {
    @Override
    public int getCost() {
        return 10;
    }

    @Override
    public String getDescription() {
        return "Simple coffee";
    }
}
