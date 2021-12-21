package ru.ramprox.behavioral.observer.observable;

import ru.ramprox.behavioral.observer.JobPost;
import ru.ramprox.behavioral.observer.observer.Observer;

public interface Observable {
    void notify(JobPost jobPosting);
    void attach(Observer observer);
    void addJob(JobPost jobPosting);
}
