package com.yy5.employee.service;

import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    EmployeeService employeeService;
    @Mock
    EmployeeMapper employeeMapper;

    //Read機能のテスト
    @Test
    void 社員情報が全件取得できること() {
        List<Employee> employees = List.of(
                new Employee("テスト1", 21),
                new Employee("テスト2", 22),
                new Employee("テスト3", 23)
        );

        doReturn(employees).when(employeeMapper).findAll();
        List<Employee> actual = employeeService.findAll();
        assertThat(actual).isEqualTo(employees);
        verify(employeeMapper).findAll();
    }
}