package ru.ramprox.behavioral.strategy.sortstrategy;

public class QuickSortStrategy<T> implements SortStrategy<T> {
    @Override
    public T[] sort(T[] dataset) {
        System.out.println("Sorting using quick sort");
        return dataset;
    }
}
