package ru.ramprox.behavioral.observer.observable;

import ru.ramprox.behavioral.observer.JobPost;
import ru.ramprox.behavioral.observer.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class EmploymentAgency implements Observable {

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void notify(JobPost jobPosting) {
        observers.forEach(observer -> observer.onJobPosted(jobPosting));
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void addJob(JobPost jobPosting) {
        notify(jobPosting);
    }
}
