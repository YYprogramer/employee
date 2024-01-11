package com.yy5.employee.service;


import com.yy5.employee.NotFound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    public EmployeeService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public List<Employee> findAll() {
        return this.employeeMapper.findAll();
    }
    public Employee findEmployee(int employeeNumber){
        Optional<Employee> employee = this.employeeMapper.findByEmployee(employeeNumber);
        return this.employeeMapper.findByEmployee(employeeNumber)
                .orElseThrow(() -> new EmployeeNotFoundException("EmployeeNumber:" + employeeNumber +" is not found"));
    }

    public Employee insert (String name, Object age ) throws IllegalAccessException {
        if (!(age instanceof Integer)) {
            throw new IllegalAccessException("年齢には整数を入力してください");
        }
        Employee employee = new Employee(name , (Integer) age);
        employeeMapper.insert(employee);
        return employee;
    }
}

