package com.examples.employee.controller;

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
		employeeRepository.findAll();
	}
}