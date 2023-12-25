package com.yy5.employee.controller;

import com.yy5.employee.entity.Employee;
import com.yy5.employee.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    public final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees/{employeeNumber}")
    public Employee getEmployee(@PathVariable("employeeNumber") int employeeNumber) {
        return employeeService.findEmployee(employeeNumber);
    }
}
