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
import java.util.Optional;

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
                        new Employee(1,"スティーブ",21),
                        new Employee(2,"マーク",20),
                        new Employee(3,"ジェフ",30)
                );
    }

    // Readテスト 指定した社員番号が存在する場合
    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在する社員番号を検索した時に正常な社員情報が取得できること() {
        Optional<Employee> employees = employeeMapper.findByEmployee(1);
        assertThat(employees).contains(new Employee(1,"スティーブ",21));
    }

    // Readテスト 指定した社員番号が存在しない場合
    @Test
    @DataSet(value = "datasets/employees.yml")
    @Transactional
    void 存在しない社員番号を検索した時にからの情報をレスポンスすること() {
        Optional<Employee> employees = employeeMapper.findByEmployee(4);
        assertThat(employees).isEmpty();
    }
}