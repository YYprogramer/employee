package com.yy5.employee.request;

import jakarta.validation.constraints.NotBlank;

public class EmployeeRequest {
    @NotBlank
    private String name;
    @NotBlank
    private int age;

    public EmployeeRequest(String name, int age) {
        this.name = name;
        this.age = age;
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
