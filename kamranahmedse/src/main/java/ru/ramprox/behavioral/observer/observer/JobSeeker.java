package ru.ramprox.behavioral.observer.observer;

import ru.ramprox.behavioral.observer.JobPost;

public class JobSeeker implements Observer {

    private String name;

    public JobSeeker(String name) {
        this.name = name;
    }

    @Override
    public void onJobPosted(JobPost job) {
        System.out.println("Hi " + name + "! New job posted: " + job.getTitle());
    }
}
