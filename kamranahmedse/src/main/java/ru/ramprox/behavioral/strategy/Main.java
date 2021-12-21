package ru.ramprox.behavioral.strategy;

import ru.ramprox.behavioral.strategy.sortstrategy.BubbleSortStrategy;
import ru.ramprox.behavioral.strategy.sortstrategy.QuickSortStrategy;

public class Main {
    public static void main(String[] args) {
        Integer[] dataset = new Integer[]{1, 5, 4, 3, 2, 8};
        Sorter<Integer> sorter = new Sorter<>(new BubbleSortStrategy<>());
        sorter.sort(dataset);

        sorter = new Sorter<>(new QuickSortStrategy<>());
        sorter.sort(dataset);
    }
}
