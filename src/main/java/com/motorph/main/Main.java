package com.motorph.main;

import com.motorph.model.Employee;
import com.motorph.model.Request;
import com.motorph.service.EmployeeService;
import com.motorph.service.RequestService;
import com.motorph.util.AppContext;
import java.util.List;
import static com.motorph.util.Util.print;
import java.util.function.Consumer;

/**
 *
 * @author Ducktavian
 */
public class Main {

    public static void main(String[] args) {
        EmployeeService empService = AppContext.getEmployeeService();
        List<Employee> list = empService.getAllEmployees();
        
        RequestService reqService = AppContext.getRequestService();
        List<Request> request = reqService.findAll();
        
        System.out.println("Start");
        forEachItem(request);
        System.out.println("End");
    }
    
    // Generic method: T represents any type (Employee, Attendance, String, etc.)
    public static <T> void forEachItem(List<T> list) {
        if (list != null) {
            for (T item : list) {
                print(String.valueOf(item));
            }
        }
    }
}