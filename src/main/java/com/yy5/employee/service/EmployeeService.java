package com.yy5.employee.service;


import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    public EmployeeService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public Employee findEmployee(int employeeNumber){
        Optional<Employee> employee = this.employeeMapper.findByEmployee(employeeNumber);
        return employee.get();
    }
}

