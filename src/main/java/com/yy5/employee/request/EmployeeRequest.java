package com.yy5.employee.request;

import com.yy5.employee.validation.ValidEmployeeAge;
import com.yy5.employee.validation.ValidEmployeeName;

public class EmployeeRequest {
    @ValidEmployeeName
    private String name;
    @ValidEmployeeAge
    private Integer age;

    public EmployeeRequest(String name, Integer age) {
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
