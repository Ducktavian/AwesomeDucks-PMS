package com.motorph.main;

import com.motorph.model.Employee;
import com.motorph.service.EmployeeService;
import com.motorph.util.AppContext;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public class Main {

    public static void main(String[] args) {
        EmployeeService empService = AppContext.getEmployeeService();
        List<Employee> list = empService.getAllEmployees();
        
        System.out.println("Start");
        for (int i = 0; i < list.size(); i++ ) {
            System.out.println(list.get(i).getEmployeeId());
        }
        System.out.println("End");
    }
}