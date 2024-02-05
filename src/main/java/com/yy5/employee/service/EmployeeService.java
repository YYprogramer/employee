package com.yy5.employee.service;


import com.yy5.employee.notfound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
    public Employee findEmployee(int employeeNumber){
        Optional<Employee> employee = this.employeeMapper.findByEmployee(employeeNumber);
        return this.employeeMapper.findByEmployee(employeeNumber)
                .orElseThrow(() -> new EmployeeNotFoundException("EmployeeNumber:" + employeeNumber +" is not found"));
    }

    public Employee insert (String name, int age ) {
        Employee employee = new Employee(name , age);
        employeeMapper.insert(employee);
        return employee;
    }
}

