package com.examples.employee.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;
import com.examples.employee.view.EmployeeView;

class EmployeeControllerTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private EmployeeView employeeView;

	@InjectMocks
	private EmployeeController employeeController;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	void testAllEmployees() {
		List<Employee> employees = asList(new Employee("1", "test"));
		when(employeeRepository.findAll()).thenReturn(employees);
		
		employeeController.allEmployees();
		
		verify(employeeView).showAllEmployees(employees);
	}
	@Test
	void testNewEmployeeWhenEmployeeDoesNotAlreadyExist() {
	    Employee employee = new Employee("1", "test");
	    when(employeeRepository.findById("1")).thenReturn(null);
	    
	    employeeController.newEmployee(employee);
	    
	    InOrder inOrder = inOrder(employeeRepository, employeeView);
	    inOrder.verify(employeeRepository).save(employee);
	    inOrder.verify(employeeView).employeeAdded(employee);
	}
}