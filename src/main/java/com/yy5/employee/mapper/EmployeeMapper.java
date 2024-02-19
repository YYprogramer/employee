package com.yy5.employee.mapper;

import com.yy5.employee.entity.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeMapper {
    @Select("select * from employees")
    List<Employee> findAll();

    @Select("select * from employees WHERE employeeNumber LIKE CONCAT('%', #{employeeNumber}, '%')")
    Optional<Employee> findByEmployee(int employeeNumber);

    @Insert("INSERT INTO employees (name, age) VALUES (#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "employeeNumber")
    void insert(Employee employee);

    @Update("UPDATE employees SET name = #{name}, age = #{age} WHERE employeeNumber = #{employeeNumber}")
    void update(int employeeNumber, String name, Integer age);

    @Delete("DELETE FROM employees WHERE employeeNumber = #{employeeNumber}")
    void delete(Integer employeeNumber);
}
