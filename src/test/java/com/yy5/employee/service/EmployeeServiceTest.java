package com.yy5.employee.service;

import com.yy5.employee.entity.Employee;
import com.yy5.employee.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @InjectMocks
    EmployeeService employeeService;

    @Mock
    EmployeeMapper employeeMapper;

    @Test
    public void 存在する社員番号を指定した時正常にユーザーが返されること() {
        doReturn(Optional.of(new Employee(1, "hoge", 20))).when(employeeMapper).findByEmployee(1);
        Employee actual = employeeService.findEmployee(1);
        assertThat(actual).isEqualTo(new Employee(1, "hoge", 20));
        verify(employeeMapper).findByEmployee(1);
    }
}
