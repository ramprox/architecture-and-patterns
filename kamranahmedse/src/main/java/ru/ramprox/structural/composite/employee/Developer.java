package ru.ramprox.structural.composite.employee;

import java.util.Set;

public class Developer implements Employee {

    protected float salary;
    protected String name;
    protected Set<String> roles;

    public Developer(String name, float salary) {
        this.salary = salary;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSalary(float salary) {
        this.salary = salary;
    }

    @Override
    public float getSalary() {
        return salary;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }
}
