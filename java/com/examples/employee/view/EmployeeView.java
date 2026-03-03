package com.examples.employee.view;

import java.util.List;
import com.examples.employee.model.Employee;

public interface EmployeeView {
    void showAllEmployees(List<Employee> employees);

    void employeeAdded(Employee employee);

    void employeeRemoved(Employee employee);

    void showError(String message, Employee employee);
}