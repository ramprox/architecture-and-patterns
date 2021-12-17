package ru.ramprox.structural.flyweight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeaShop {
    protected Map<Integer, KarakTea> orders = new HashMap<>();

    protected TeaMaker teaMaker;

    public TeaShop(TeaMaker teaMaker) {
        this.teaMaker = teaMaker;
    }

    public void takeOrder(String teaType, int table) {
        orders.put(table, teaMaker.make(teaType));
    }

    public void serve() {
        orders.keySet().forEach(key -> System.out.println("Serving tea to table# " + key));
    }
}
