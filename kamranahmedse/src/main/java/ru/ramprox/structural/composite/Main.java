package ru.ramprox.structural.composite;

import ru.ramprox.structural.composite.employee.Designer;
import ru.ramprox.structural.composite.employee.Developer;
import ru.ramprox.structural.composite.employee.Employee;

public class Main {
    public static void main(String[] args) {
        Employee john = new Developer("John Doe", 12000);
        Employee jane = new Designer("Jane Doe", 15000);
        Organization organization = new Organization();
        organization.addEmployee(john);
        organization.addEmployee(jane);
        System.out.println(organization.getNetSalaries());
    }
}
