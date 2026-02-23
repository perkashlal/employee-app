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
}