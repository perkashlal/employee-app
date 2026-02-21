package com.examples.employee.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.examples.employee.model.Employee;

class EmployeeMongoRepositoryTest {

	private EmployeeMongoRepository employeeRepository;

	@BeforeEach
	void setup() {
		employeeRepository = new EmployeeMongoRepository();
	}

	@Test
	void testFindAllWhenDatabaseIsEmpty() {
		List<Employee> employees = employeeRepository.findAll();
		assertThat(employees).isEmpty();
	}
	@Test
	void testFindAllWhenDatabaseIsNotEmpty() {
		
		Employee employee = new Employee("1", "pika");
		List<Employee> employees = employeeRepository.findAll();
		
		assertThat(employees).containsExactly(employee);
	}
}