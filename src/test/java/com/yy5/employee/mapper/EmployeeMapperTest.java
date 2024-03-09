package com.yy5.employee.mapper;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.yy5.employee.entity.Employee;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataSet(value = "datasets/employees.yml")
class EmployeeMapperTest {
    @Autowired
    EmployeeMapper employeeMapper;

    @Test
    @Transactional
    void 全ての社員情報が取得できること() {
        List<Employee> employees = employeeMapper.findAll();
        assertThat(employees)
                .hasSize(3)
                .contains(
                        new Employee(1,"スティーブ",21),
                        new Employee(2,"マーク",20),
                        new Employee(3,"ジェフ",30)
                );
    }

    @Test
    @Transactional
    void 存在する社員番号を検索した時に正常な社員情報が取得できること() {
        Optional<Employee> employees = employeeMapper.findById(1);
        assertThat(employees).contains(new Employee(1,"スティーブ",21));
    }

    @Test
    @Transactional
    void 存在しない社員番号を検索した時に空の情報をレスポンスすること() {
        Optional<Employee> employees = employeeMapper.findById(4);
        assertThat(employees).isEmpty();
    }

    @Test
    @ExpectedDataSet(value = "datasets/insertEmployeeTest.yml", ignoreCols = "employeeNumber")
    @Transactional
    public void クリエイトリクエストが行われ得たとき新しい社員が登録できること() {
        Employee employee = new Employee("iwatsuki",29);
        employeeMapper.insert(employee);
    }
}
