package integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.jayway.jsonpath.JsonPath;
import com.yy5.employee.EmployeeApplication;
import com.yy5.employee.controller.request.EmployeeRequest;
import com.yy5.employee.mapper.EmployeeMapper;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import org.apache.ibatis.annotations.Param;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = EmployeeApplication.class)
@AutoConfigureMockMvc
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRestApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void getallがリクエストされた時社員情報が全件取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals("""
                         [
                         {"employeeNumber":1,"name":"スティーブ","age":21},
                         {"employeeNumber":2,"name":"マーク","age":20},
                         {"employeeNumber":3,"name":"ジェフ","age":30}
                         ]
                        """
                , response, JSONCompareMode.STRICT);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員番号がリクエストされた時に該当の社員情報が取得できること() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals("""
                         {"employeeNumber":1,"name":"スティーブ","age":21}
                        """
                , response, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    @ValueSource(ints = {0,100})
    void 存在しない社員番号がリクエストされた時に404をレスポンスすること(int employeeNumber) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/employees/" + employeeNumber))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("EmployeeNumber:" + employeeNumber + " is not found", JsonPath.read(response, "$.message"));
        assertEquals("/employees/" + employeeNumber , JsonPath.read(response, "$.path"));
        assertEquals("Not Found", JsonPath.read(response, "$.error"));
    }

    @Test
    @DataSet(value = "datasets/employees.yml", cleanBefore = true, cleanAfter = true)
    @Transactional
    void クリエイトリクエストを受け取ったとき社員情報を登録する() throws Exception {
        EmployeeRequest request = new EmployeeRequest("iwatsuki",29);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        MvcResult postResult = (MvcResult) mockMvc.perform(MockMvcRequestBuilders.post("/employees")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(MockMvcResultMatchers.status().isCreated())
               .andReturn();

        String locationHeader = postResult.getResponse().getHeader("Location");
        String[] locationParts = locationHeader.split("/");
        int id = Integer.parseInt(locationParts[locationParts.length - 1]);
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/employees/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = getResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(id, (Integer) JsonPath.read(response, "$.employeeNumber"));
        assertEquals("iwatsuki", JsonPath.read(response, "$.name"));
        assertEquals(29, (Integer) JsonPath.read(response, "$.age"));
    }

    @ParameterizedTest
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    @NullSource
    void クリエイトリクエストを受け取ったとき名前情報及び年齢情報がnullだとバリデーションが実行されること(Integer age, String name) throws Exception {
        EmployeeRequest request = new EmployeeRequest(name, age);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(request);

            mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))

                    .andExpect(MockMvcResultMatchers.status().isBadRequest())

                    .andExpect(jsonPath("$.message").value("validation error"));
    }

    @ParameterizedTest
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    @NullSource
    void クリエイトリクエストを受け取ったとき名前情報及nullだとバリデーションが実行されること(String name) throws Exception {
        EmployeeRequest request = new EmployeeRequest(name, 29);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"));
    }
}
