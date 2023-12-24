package com.yy5.employee.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    @Select("select * from emplyee_list WHRER name LIKE CONCAT('%', #{name}, '%')")
    List<Employee> findByName(String name);
}
