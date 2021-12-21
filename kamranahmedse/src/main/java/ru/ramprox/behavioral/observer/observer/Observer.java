package ru.ramprox.behavioral.observer.observer;

import ru.ramprox.behavioral.observer.JobPost;

public interface Observer {
    void onJobPosted(JobPost job);
}
