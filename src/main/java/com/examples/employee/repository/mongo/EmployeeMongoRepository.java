package com.examples.employee.repository.mongo;

import java.util.List;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;
import java.util.Collections;
public class EmployeeMongoRepository implements EmployeeRepository {

	@Override
	public List<Employee> findAll() {
		return Collections.emptyList();
	}
}