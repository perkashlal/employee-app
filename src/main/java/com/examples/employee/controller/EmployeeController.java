package com.examples.employee.controller;

import java.util.List;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;
import com.examples.employee.view.EmployeeView;

public class EmployeeController {

	private EmployeeRepository employeeRepository;
	private EmployeeView employeeView;

	public EmployeeController(EmployeeRepository employeeRepository, EmployeeView employeeView) {
		this.employeeRepository = employeeRepository;
		this.employeeView = employeeView;
	}

	public void allEmployees() {
		List<Employee> employees = employeeRepository.findAll();
		employeeView.showAllEmployees(employees);
		
	}
	public void newEmployee(Employee employee) {
	    Employee existingEmployee = employeeRepository.findById(employee.getId());
	    if (existingEmployee == null) {
	        employeeRepository.save(employee);
	        employeeView.employeeAdded(employee);
	    } else {
	        employeeView.showError("Already existing employee with id " + employee.getId(), 
	            existingEmployee);
	    }
	    
	}
	public void deleteEmployee(Employee employee) {
	    if (employeeRepository.findById(employee.getId()) != null) {
	        employeeRepository.delete(employee.getId());
	        employeeView.employeeRemoved(employee);
	    }
	}
}