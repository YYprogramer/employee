package com.yy5.employee.entity;

public class Employee {
    private int employeeNumber;
    private String name;
    private int age;

    public Employee(Integer employeeNumber, String name, int age) {
        this.employeeNumber = employeeNumber;
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
}
