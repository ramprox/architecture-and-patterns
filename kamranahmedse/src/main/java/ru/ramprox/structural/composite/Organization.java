package ru.ramprox.structural.composite;

import ru.ramprox.structural.composite.employee.Employee;

import java.util.LinkedList;
import java.util.List;

public class Organization {

    protected List<Employee> employees = new LinkedList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public float getNetSalaries() {
        return (float) employees.stream()
                .mapToDouble(Employee::getSalary)
                .sum();
    }
}
