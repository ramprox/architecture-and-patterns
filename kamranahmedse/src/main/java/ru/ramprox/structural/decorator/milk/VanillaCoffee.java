package ru.ramprox.structural.decorator.milk;

public class VanillaCoffee implements Coffee {

    protected Coffee coffee;

    public VanillaCoffee(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public int getCost() {
        return coffee.getCost() + 3;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", vanilla";
    }
}
