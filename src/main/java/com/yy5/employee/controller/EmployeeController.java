package com.yy5.employee.controller;

import com.yy5.employee.notfound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.request.EmployeeRequest;
import com.yy5.employee.response.EmployeeResponse;
import com.yy5.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @ExceptionHandler(value = EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(
            EmployeeNotFoundException e, HttpServletRequest request) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("timestamp", ZonedDateTime.now().toString());
        body.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("message", e.getMessage());
        body.put("path", request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> insert(@RequestBody @Validated EmployeeRequest employeeRequest, UriComponentsBuilder uriBuilder)  {
            Employee employee = employeeService.insert(employeeRequest.getName(),employeeRequest.getAge());
            URI location = uriBuilder.path("employees/{employeeNumber}").buildAndExpand(employee.getEmployeeNumber()).toUri();
            EmployeeResponse body = new EmployeeResponse("employee created");
            return ResponseEntity.created(location).body(body);
    }

    @PatchMapping("/employees/{employeeNumber}")
    public  EmployeeResponse update(@RequestBody @Validated @PathVariable int employeeNumber EmployeeRequest employeeRequest) throws EmployeeNotFoundException {
        Employee employeeRequest;
        employeeService.update(employeeNumber, employeeRequest.getName(), employeeRequest.getAge());
        return new EmployeeResponse("社員情報を更新しました");
    }
}
