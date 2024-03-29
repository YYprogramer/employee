package com.yy5.employee.service;


import com.yy5.employee.controller.notfound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Employee findById (int employeeNumber){
        Optional<Employee> employee = this.employeeMapper.findById(employeeNumber);
        return employee
                .orElseThrow(() -> new EmployeeNotFoundException("EmployeeNumber:" + employeeNumber +" is not found"));
    }

    public Employee insert (String name, int age ) {
        Employee employee = new Employee(name , age);
        employeeMapper.insert(employee);
        return employee;
    }

    public void update(int employeeNumber, String name, Integer age) throws EmployeeNotFoundException {
        Employee employee = this.employeeMapper.findById(employeeNumber)
                .orElseThrow(() -> new EmployeeNotFoundException("EmployeeNumber " + employeeNumber + " is not found"));
        employeeMapper.update(employeeNumber, name, age);
    }

    public void delete(Integer employeeNumber) {
        Employee employee = this.employeeMapper.findById(employeeNumber)
                .orElseThrow(() -> new EmployeeNotFoundException("EmployeeNumber " + employeeNumber + " is not found"));
        employeeMapper.delete(employeeNumber);
    }
}