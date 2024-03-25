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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    EmployeeService employeeService;
    @Mock
    EmployeeMapper employeeMapper;

    // Read機能のテスト
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

    @Test
    void 存在する社員番号を検索した場合に社員の名前と年齢の情報が取得できること() {
        doReturn(Optional.of(new Employee(1,"テスト1",21))).when(employeeMapper).findById(1);
        Employee actual = employeeService.findById(1);
        assertThat(actual).isEqualTo(new Employee(1,"テスト1",21));
        verify(employeeMapper).findById(1);
    }

    @Test
    void 存在しない社員番号を検索した場合に例外処理が動作すること() throws EmployeeNotFoundException {
        doReturn(Optional.empty()).when(employeeMapper).findById(99);
        assertThrows(EmployeeNotFoundException.class,() -> {
            employeeService.findById(99);
        });
        verify(employeeMapper).findById(99);
    }

    @Test
    void クリエイトリクエストを受け取ったとき社員情報を登録すること() {
        Employee employee = new Employee("iwatsuki",29);
        assertThat(employeeService.insert("iwatsuki",29)).isEqualTo(employee);
        verify(employeeMapper).insert(employee);
    }

    @Test
    void  存在する社員情報を更新するリクエストを受け取ったとき社員情報を更新する() {
        Employee updateEmployee = new Employee(1,"更新前社員",20);

        doReturn(Optional.of(updateEmployee)).when(employeeMapper).findById(1);

        employeeService.update(1,"更新後社員",30);

        verify(employeeMapper).update(1,"更新後社員",30);
    }

    @Test
    void  存在しない社員情報を更新するリクエストを受け取ったとき404エラーをレスポンスする() {
        int notFoundEmployeeNumber = 100;
        String errorMessage = "EmployeeNumber " + notFoundEmployeeNumber + " is not found";

        doReturn(Optional.empty()).when(employeeMapper).findById(notFoundEmployeeNumber);

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.update(notFoundEmployeeNumber, "新しい名前", 25);
        },"404 NOT_FOUND");
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(employeeMapper).findById(notFoundEmployeeNumber);
    }

    @Test
    void 存在する社員情報を削除するリクエストを受け取ったとき社員情報を削除する() {
        Employee deleteEmployee = new Employee(1,"削除社員情報",20);
        doReturn(Optional.of(deleteEmployee))
                .when(employeeMapper).findById(1);
        employeeService.delete(1);
        verify(employeeMapper).delete(1);
    }

    @Test
    void 存在しない社員情報を削除するリクエストを受け取ったとき例外処理のエラーメッセージをレスポンスする() {
        doReturn(Optional.empty()).when(employeeMapper).findById(100);
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.delete(100));
        assertEquals("EmployeeNumber 100 is not found", exception.getMessage());

        verify(employeeMapper, times(1)).findById(100);
        verify(employeeMapper, times(0)).delete(100);
    }
}
