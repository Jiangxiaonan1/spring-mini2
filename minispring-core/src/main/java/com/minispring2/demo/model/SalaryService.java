package com.minispring2.demo.model;

import com.minispring2.annotation.Autowired;
import com.minispring2.annotation.Component;

/**
 *
 * @since 2026-11-11 15:06:00
 **/
@Component
public class SalaryService {

    @Autowired
    EmployeeService employeeService;

    public void printSalary() {
        employeeService.getName();
        System.out.println("调用printSalary方法");
    }
}
