package com.yy5.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yy5.employee.controller.request.EmployeeRequest;
import com.yy5.employee.controller.response.EmployeeResponse;
import com.yy5.employee.entity.Employee;
import com.yy5.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void クリエイトリクエストを受け取ったとき社員情報を登録すること() {
        //モックの設定
        when(employeeService.insert(anyString(), anyInt())).thenReturn(new Employee("iwatsuki", 29));

        //テスト対象メソッドの呼び出し
        EmployeeRequest request = new EmployeeRequest("iwatsuki", 29);
        ResponseEntity<EmployeeResponse> responseEntity = employeeController.insert(request, UriComponentsBuilder.newInstance());

        //検証
        verify(employeeService, times(1)).insert(eq("iwatsuki"), eq(29));
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("employee created", responseEntity.getBody().getMessage());
    }

    @Test
    void クリエイトリクエストを受けっとたとき名前及び年齢情報がnullだとバリデーションが実行される() throws Exception {
        EmployeeRequest request = new EmployeeRequest(null,null);
        MvcResult mvcResult = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        verify(employeeService, never()).insert(anyString(),anyInt());
    }

    @Test
    void クリエイトリクエストを受けっとたとき名前がnullだとバリデーションが実行される() throws Exception {
        EmployeeRequest request = new EmployeeRequest(null,29);
        MvcResult mvcResult = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        verify(employeeService, never()).insert(anyString(),anyInt());
    }

    @Test
    void クリエイトリクエストを受けっとたとき年齢がnullだとバリデーションが実行される() throws Exception {
        EmployeeRequest request = new EmployeeRequest("iwatsuki",null);
        MvcResult mvcResult = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        verify(employeeService, never()).insert(anyString(),anyInt());
    }

    @Test
    void クリエイトリクエストを受けっとたとき名前が空文字だとバリデーションが実行される() throws Exception {
        EmployeeRequest request = new EmployeeRequest(" ",29);
        MvcResult mvcResult = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        verify(employeeService, never()).insert(anyString(),anyInt());
    }
}
