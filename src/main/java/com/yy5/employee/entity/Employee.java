package com.yy5.employee.entity;

import java.util.Objects;

public class Employee {
    private int employeeNumber;
    private String name;
    private int age;

    public Employee(int employeeNumber, String name, int age) {
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.age = age;
    }

    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        };
        if (o == null || getClass() != o.getClass())  {
            return false;
        };
        Employee employee = (Employee) o;
        return employeeNumber == employee.employeeNumber && age == employee.age && Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber, name, age);
    }
}
