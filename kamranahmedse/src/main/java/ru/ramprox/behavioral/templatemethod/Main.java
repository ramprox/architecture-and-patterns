package ru.ramprox.behavioral.templatemethod;

import ru.ramprox.behavioral.templatemethod.builder.AndroidBuilder;
import ru.ramprox.behavioral.templatemethod.builder.Builder;
import ru.ramprox.behavioral.templatemethod.builder.IosBuilder;

public class Main {
    public static void main(String[] args) {
        Builder androidBuilder = new AndroidBuilder();
        androidBuilder.build();
        Builder iosBuilder = new IosBuilder();
        iosBuilder.build();
    }
}
