package com.yy5.employee.controller;

import com.yy5.employee.controller.request.EmployeeRequest;
import com.yy5.employee.controller.response.EmployeeResponse;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Test
    void クリエイトリクエストを受け取ったとき社員情報を登録すること() {
        // モックの準備
        when(employeeService.insert(anyString(), anyInt())).thenReturn(new Employee("iwatsuki", 29));

        // テスト実行
        EmployeeRequest request = new EmployeeRequest("iwatsuki", 29);
        ResponseEntity<EmployeeResponse> responseEntity = employeeController.insert(request, UriComponentsBuilder.newInstance());

        // モックの検証
        verify(employeeService, times(1)).insert(eq("iwatsuki"), eq(29));

        // レスポンスの検証
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("employee created", responseEntity.getBody().getMessage());
    }


}
