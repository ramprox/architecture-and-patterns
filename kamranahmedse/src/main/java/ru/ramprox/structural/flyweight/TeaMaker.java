package ru.ramprox.structural.flyweight;

import java.util.*;

public class TeaMaker {
    protected Map<String, KarakTea> availableTea = new HashMap<>();

    public KarakTea make(String preference) {
        return availableTea.computeIfAbsent(preference, key -> new KarakTea());
    }
}
