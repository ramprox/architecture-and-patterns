package ru.ramprox.behavioral.strategy.sortstrategy;

public class BubbleSortStrategy<T> implements SortStrategy<T> {
    @Override
    public T[] sort(T[] dataset) {
        System.out.println("Sorting using bubble sort");
        return dataset;
    }
}
