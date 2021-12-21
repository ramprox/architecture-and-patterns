package ru.ramprox.behavioral.strategy;

import ru.ramprox.behavioral.strategy.sortstrategy.SortStrategy;

public class Sorter<T> {
    private SortStrategy<T> sorter;

    public Sorter(SortStrategy<T> sorter) {
        this.sorter = sorter;
    }

    public T[] sort(T[] dataset) {
        return sorter.sort(dataset);
    }
}
