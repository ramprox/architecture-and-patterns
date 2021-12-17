package ru.ramprox.structural.composite.employee;

import java.util.Set;

public interface Employee {
    String getName();
    void setSalary(float salary);
    float getSalary();
    Set<String> getRoles();
}
