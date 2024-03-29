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
    public void クリエイトリクエストを受け取ったとき新しい社員が登録できること() {
        Employee employee = new Employee("iwatsuki",29);
        employeeMapper.insert(employee);
        Optional<Employee> actualEmployee = employeeMapper.findById(employee.getEmployeeNumber());
    }

    @Test
    @ExpectedDataSet(value = "datasets/updateEmployeeTest.yml", ignoreCols = "employeeNumber")
    @Transactional
    public void 存在する社員情報を更新するリクエストを受け取ったとき社員情報を更新する() {
        Optional<Employee> updateEmployee = employeeMapper.findById(1);
        assertThat(updateEmployee).isEqualTo(Optional.of(new Employee(1,"スティーブ",21)));
        employeeMapper.update(1,"ジョブズ",22);
        Optional<Employee> updatedEmployee = employeeMapper.findById(1);
    }

    @Test
    @DataSet(value = "datasets/employees.yml")
    @ExpectedDataSet(value = "datasets/deleteEmployeeTest.yml")
    @Transactional
    void 存在する社員情報を削除するリクエストを受け取ったとき社員情報を削除する() {
        employeeMapper.delete(1);
    }

    @Test
    @DataSet(value ="datasets/employees.yml")
    @ExpectedDataSet(value ="datasets/employees.yml")
    @Transactional
    public void 存在しない映画情報を削除した場合はテーブルの既存レコードは削除されないこと(){
        employeeMapper.delete(100);
    }

}
