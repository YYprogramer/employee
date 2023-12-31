package com.yy5.employee.mapper;

import com.yy5.employee.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

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
}
