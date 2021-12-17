package ru.ramprox.structural.decorator.milk;

public class WhipCoffee implements Coffee {

    protected Coffee coffee;

    public WhipCoffee(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public int getCost() {
        return coffee.getCost() + 5;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", whip";
    }
}
