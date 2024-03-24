package integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.jayway.jsonpath.JsonPath;
import com.yy5.employee.EmployeeApplication;
import com.yy5.employee.controller.request.EmployeeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    @DataSet(value = "datasets/employees.yml")
    @ExpectedDataSet(value = "datasets/updateEmployees.yml")
    @Transactional
    void 存在する社員情報を更新するリクエストを受け取ったとき社員情報を更新する() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                    "employeeNumber": 1,
                                    "name": "更新後社員",
                                    "age": 30
                                    }
                                """))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                {
                    "message":"社員情報を更新しました"
                }
                """, response, JSONCompareMode.STRICT);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在しない社員情報を更新するリクエストを受け取ったとき404エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/100")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "employeeNumber": 100,
                                    "name": "更新後社員",
                                    "age": 30
                                    }
                                """))
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONAssert.assertEquals("""
                {
                     "message": "EmployeeNumber 100 is not found",
                     "timestamp": "2024-03-22T19:00:01.451993+09:00[Asia/Tokyo]",
                     "error": "Not Found",
                     "path": "/employees/100",
                     "status": "404"
                }
                """, response, new CustomComparator(JSONCompareMode.STRICT,
                new Customization("timestamp",(((o1, o2) -> true)))));
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員情報の名前だけ更新するリクエストを受け取ったとき400エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "employeeNumber": 1,
                                    "name": "更新後社員",
                                    "age": null
                                    }
                                """))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員情報の年齢だけ更新するリクエストを受け取ったとき400エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "employeeNumber": 1,
                                    "name": null,
                                    "age": 30
                                    }
                                """))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員情報の年齢を17歳にしてリクエストを受け取ったとき400エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "employeeNumber": 1,
                                    "name": "更新後社員",
                                    "age": 17
                                    }
                                """))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員情報の年齢を66歳にしてリクエストを受け取ったとき400エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                            "employeeNumber": 1,
                                            "name": "更新後社員",
                                            "age": 66
                                            }
                                        """))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員情報の年齢を18歳にしてリクエストを受け取ったとき400エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                    "employeeNumber": 1,
                                    "name": "更新後社員",
                                    "age": 18
                                    }
                                """))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員情報の年齢を65歳にしてリクエストを受け取ったとき400エラーをレスポンスする() throws Exception {
        String response =
                mockMvc.perform(MockMvcRequestBuilders.patch("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                            "employeeNumber": 1,
                                            "name": "更新後社員",
                                            "age": 65
                                            }
                                        """))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @ExpectedDataSet(value = "datasets/deleteEmployeeTest.yml")
    @Transactional
    void 存在する社員情報を削除するリクエストを受け取ったとき社員情報を削除する() throws Exception{
        assertTrue(mockMvc.perform(MockMvcRequestBuilders.delete("/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Employee  deleted"));
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals("""
                [
                    {
                        "employeeNumber": 2,
                        "name": "マーク",
                        "age": 20
                    },
                    {
                        "employeeNumber": 3,
                        "name": "ジェフ",
                        "age": 30
                    }
                ]
                """, response, JSONCompareMode.STRICT);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在しない社員情報を削除するリクエストを受け取ったとき404エラーをレスポンスする() throws Exception{
        assertTrue(mockMvc.perform(MockMvcRequestBuilders.delete("/employees/100"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn().getResponse().getContentAsString().contains("EmployeeNumber 100 is not found"));
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/employees"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals("""
                [
                    {
                        "employeeNumber": 1,
                        "name": "スティーブ",
                        "age": 21
                    },
                    {
                        "employeeNumber": 2,
                        "name": "マーク",
                        "age": 20
                    },
                    {
                        "employeeNumber": 3,
                        "name": "ジェフ",
                        "age": 30
                    }
                ]
                """, response, JSONCompareMode.STRICT);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void nullで社員情報を削除するリクエストを受け取ったとき400エラーをレスポンスする() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/employees/null"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 空文字で社員情報を削除するリクエストを受け取ったとき400エラーをレスポンスする() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/employees/ "))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 漢数字で社員情報を削除するリクエストを受け取ったとき400エラーをレスポンスする() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/employees/一"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }
}
