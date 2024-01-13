package com.yy5.employee.NotCreated;

public class EmployeeNotCreated extends RuntimeException {
    public EmployeeNotCreated (String message) {
        super(message);
    }
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
