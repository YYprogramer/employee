package integrationtest;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.yy5.employee.EmployeeApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

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
}
