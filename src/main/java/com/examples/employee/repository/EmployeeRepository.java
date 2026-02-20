package com.examples.employee.repository;

import java.util.List;
import com.examples.employee.model.Employee;

public interface EmployeeRepository {
	public List<Employee> findAll();
}