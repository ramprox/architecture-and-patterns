package ru.ramprox.structural.adapter.wilddog;

import ru.ramprox.structural.adapter.lion.Lion;

public class WildDogAdapter implements Lion {

    private WildDog wildDog;

    public WildDogAdapter(WildDog wildDog) {
        this.wildDog = wildDog;
    }

    @Override
    public void roar() {
        wildDog.bark();
    }
}
