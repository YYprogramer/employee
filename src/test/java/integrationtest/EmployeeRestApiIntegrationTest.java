package integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.jayway.jsonpath.JsonPath;
import com.yy5.employee.EmployeeApplication;
import com.yy5.employee.controller.request.EmployeeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void 全社員情報を取得するリクエストを受け取った時社員情報が全件取得できること() throws Exception {
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
    void クリエイトリクエストを受け取ったとき名前情報がnullだとバリデーションが実行されること(String name) throws Exception {
        EmployeeRequest request = new EmployeeRequest(name, 29);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("無効な名前です"));
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void クリエイトリクエストを受け取ったとき年齢情報がnullだとバリデーションが実行されること() throws Exception {

        String requestBody = "{\"name\": \"iwatsuki\", \"age\": null}";

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"))
                .andExpect(jsonPath("$.errors[0].field").value("age"))
                .andExpect(jsonPath("$.errors[0].message").value("無効な年齢です"));
    }

    @ParameterizedTest
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    @EmptySource
    void クリエイトリクエストを受け取ったとき名前情報が空文字だとバリデーションが実行されること(String name) throws Exception {
        EmployeeRequest request = new EmployeeRequest(name, 29);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("無効な名前です"));
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void クリエイトリクエストを受け取ったとき年齢情報が空文字だとバリデーションが実行されること() throws Exception {

        String requestBody = "{\"name\": \"iwatsuki\", \"age\": \" \"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"))
                .andExpect(jsonPath("$.errors[0].field").value("age"))
                .andExpect(jsonPath("$.errors[0].message").value("無効な年齢です"));
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void クリエイトリクエストを受け取ったとき年齢が17歳だとバリデーションが実行されること() throws Exception {
        EmployeeRequest request = new EmployeeRequest("テスト", 17);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"))
                .andExpect(jsonPath("$.errors[0].field").value("age"))
                .andExpect(jsonPath("$.errors[0].message").value("無効な年齢です"));
    }

    @Test
    @DataSet(value = "datasets/employees.yml", cleanBefore = true, cleanAfter = true)
    @Transactional
    void クリエイトリクエストを受け取ったとき年齢情報が18歳だと登録すること() throws Exception {
        EmployeeRequest request = new EmployeeRequest("正常テスト",18);

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
        assertEquals("正常テスト", JsonPath.read(response, "$.name"));
        assertEquals(18, (Integer) JsonPath.read(response, "$.age"));
    }

    @Test
    @DataSet(value = "datasets/employees.yml", cleanBefore = true, cleanAfter = true)
    @Transactional
    void クリエイトリクエストを受け取ったとき年齢情報が65歳だと登録すること() throws Exception {
        EmployeeRequest request = new EmployeeRequest("正常テスト",65);

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
        assertEquals("正常テスト", JsonPath.read(response, "$.name"));
        assertEquals(65, (Integer) JsonPath.read(response, "$.age"));
    }
    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void クリエイトリクエストを受け取ったとき年齢が66歳だとバリデーションが実行されること() throws Exception {
        EmployeeRequest request = new EmployeeRequest("テスト", 66);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(jsonPath("$.message").value("validation error"))
                .andExpect(jsonPath("$.errors[0].field").value("age"))
                .andExpect(jsonPath("$.errors[0].message").value("無効な年齢です"));
    }
}
