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

    public void update(int employeeNumber, String name, Integer age) throws EmployeeNotFoundException {
        Employee employee = findEmployee(employeeNumber);
        if (employee == null) {
            throw new EmployeeNotFoundException("employeeNumberが" + employeeNumber + "の社員は存在しません");
        }
        validateUpdateParameters(name,age);
        employeeMapper.update(employeeNumber, name, age);
    }
    public void validateUpdateParameters(String name, Integer age) {
        // nameがnullもしくは空白の場合
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }

        // ageがnullの場合
        if (age == null ) {
            throw new IllegalArgumentException("年齢は必須です");
        }
    }
}

