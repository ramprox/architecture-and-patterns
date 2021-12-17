package ru.ramprox.structural.adapter;

import ru.ramprox.structural.adapter.wilddog.WildDogAdapter;
import ru.ramprox.structural.adapter.wilddog.WildDog;

public class Main {
    public static void main(String[] args) {
        WildDog wildDog = new WildDog();
        WildDogAdapter wildDogAdapter = new WildDogAdapter(wildDog);

        Hunter hunter = new Hunter();
        hunter.hunt(wildDogAdapter);
    }
}
