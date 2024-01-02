package com.yy5.employee.controller;

import com.yy5.employee.NotFound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class EmployeeController {
    public final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public List<Employee> getAll() {
        return employeeService.findAll();
    }
    @GetMapping("/employees/{employeeNumber}")
    public Employee getEmployee(@PathVariable("employeeNumber") int employeeNumber) {
        return employeeService.findEmployee(employeeNumber);
    }
    @ExceptionHandler( value  = EmployeeNotFoundException.class )
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(
            EmployeeNotFoundException e, HttpServletRequest request) {
        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                "message", e.getMessage(),
                "path", request.getRequestURI());
        return new ResponseEntity(body, HttpStatus.NOT_FOUND);
    }
}
