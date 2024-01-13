package com.yy5.employee.controller;

import com.yy5.employee.NotCreated.EmployeeNotCreated;
import com.yy5.employee.NotFound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.request.EmployeeRequest;
import com.yy5.employee.response.EmployeeResponse;
import com.yy5.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.ErrorManager;

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
    @ExceptionHandler(value  = EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(
            EmployeeNotFoundException e, HttpServletRequest request) {
        Map<String, String> body = new LinkedHashMap<>();  // LinkedHashMapを使用する
        body.put("timestamp", ZonedDateTime.now().toString());
        body.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("message", e.getMessage());
        body.put("path", request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    @PostMapping("/employees")
    public ResponseEntity<?> insert(@RequestBody @Valid EmployeeRequest employeeRequest, UriComponentsBuilder uriBuilder) throws IllegalAccessException {
            Employee employee = employeeService.insert(employeeRequest.getName(),employeeRequest.getAge());
            URI location = uriBuilder.path("employees/{employeeNumber}").buildAndExpand(employee.getEmployeeNumber()).toUri();
            EmployeeResponse body = new EmployeeResponse("employee created");
            return ResponseEntity.created(location).body(body);
    }
}
