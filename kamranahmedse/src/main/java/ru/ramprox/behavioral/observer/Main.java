package ru.ramprox.behavioral.observer;

import ru.ramprox.behavioral.observer.observable.EmploymentAgency;
import ru.ramprox.behavioral.observer.observable.Observable;
import ru.ramprox.behavioral.observer.observer.JobSeeker;
import ru.ramprox.behavioral.observer.observer.Observer;

public class Main {
    public static void main(String[] args) {
        Observer johnDoe = new JobSeeker("John Doe");
        Observer janeDoe = new JobSeeker("Jane Doe");
        Observable jobPostings = new EmploymentAgency();
        jobPostings.attach(johnDoe);
        jobPostings.attach(janeDoe);
        jobPostings.addJob(new JobPost("Software Engineer"));
    }
}
