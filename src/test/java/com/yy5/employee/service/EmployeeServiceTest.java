package com.yy5.employee.service;

import com.yy5.employee.controller.notfound.EmployeeNotFoundException;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    // Read機能のテスト
    @Test
    // Readテスト 全件取得
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

    @Test
    // Readテスト 社員番号で検索 成功
    void 存在する社員番号を検索した場合に社員の名前と年齢の情報が取得できること() {
        doReturn(Optional.of(new Employee(1,"テスト1",21))).when(employeeMapper).findByEmployee(1);
        Employee actual = employeeService.findEmployee(1);
        assertThat(actual).isEqualTo(new Employee(1,"テスト1",21));
        verify(employeeMapper).findByEmployee(1);
    }

    @Test
    // Readテスト 社員番号で検索 失敗
    void 存在しない社員番号を検索した場合に例外処理が動作すること() throws EmployeeNotFoundException {
        doReturn(Optional.empty()).when(employeeMapper).findByEmployee(99);
        assertThrows(EmployeeNotFoundException.class,() -> {
            employeeService.findEmployee(99);
        });
        verify(employeeMapper).findByEmployee(99);
    }
}