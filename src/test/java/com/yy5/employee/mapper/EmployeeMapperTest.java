package com.yy5.employee.mapper;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.yy5.employee.entity.Employee;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeMapperTest {
    @Autowired
    EmployeeMapper employeeMapper;

    // Readテスト 全件取得
    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 全ての社員情報が取得できること() {
        List<Employee> employees = employeeMapper.findAll();
        assertThat(employees)
                .hasSize(3)
                .contains(
                        new Employee(1,"テスト1",21),
                        new Employee(2,"テスト2",22),
                        new Employee(3,"テスト3",23)
                );
    }

}