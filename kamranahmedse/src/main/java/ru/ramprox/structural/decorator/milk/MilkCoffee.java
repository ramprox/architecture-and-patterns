package ru.ramprox.structural.decorator.milk;

public class MilkCoffee implements Coffee {

    protected Coffee coffee;

    public MilkCoffee(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public int getCost() {
        return coffee.getCost() + 2;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", milk";
    }
}
