package com.examples.employee.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;
import com.examples.employee.view.EmployeeView;
import org.mockito.InOrder;
import static org.mockito.ArgumentMatchers.any;

public class EmployeeControllerTest { 

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private EmployeeView employeeView;

	@InjectMocks
	private EmployeeController employeeController;

	private AutoCloseable closeable;

	@Before
	public void setup() { // Added public
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception { // Added public
		closeable.close();
	}

	@Test
	public void testAllEmployees() { // Added public
		List<Employee> employees = asList(new Employee("1", "test"));
		when(employeeRepository.findAll()).thenReturn(employees);
		
		employeeController.allEmployees();
		
		verify(employeeView).showAllEmployees(employees);
	}
	
	@Test
	public void testNewEmployeeWhenEmployeeDoesNotAlreadyExist() {
		Employee employee = new Employee("1", "test");
		when(employeeRepository.findById("1")).thenReturn(null);
		
		employeeController.newEmployee(employee);
		
		verify(employeeRepository).save(employee);
		verify(employeeView).employeeAdded(employee);
	}

	@Test
	public void testNewEmployeeWhenEmployeeAlreadyExistsShouldShowErrorAndReturn() {
		Employee employeeToAdd = new Employee("1", "new");
		Employee existingEmployee = new Employee("1", "existing");
		when(employeeRepository.findById("1")).thenReturn(existingEmployee);

		employeeController.newEmployee(employeeToAdd);

		verify(employeeView).showError("Already existing employee with id 1", existingEmployee);
		verify(employeeRepository, never()).save(any());
		verify(employeeView, never()).employeeAdded(any());
	}

	@Test
	public void testDeleteEmployeeWhenEmployeeExists() {
		Employee employeeToDelete = new Employee("1", "test");
		when(employeeRepository.findById("1")).thenReturn(employeeToDelete);
		
		employeeController.deleteEmployee(employeeToDelete);
		
		InOrder inOrder = inOrder(employeeRepository, employeeView);
		inOrder.verify(employeeRepository).delete("1");
		inOrder.verify(employeeView).employeeRemoved(employeeToDelete);
	}

	@Test
	public void testDeleteEmployeeWhenEmployeeDoesNotExist() {
		Employee employee = new Employee("1", "test");
		when(employeeRepository.findById("1")).thenReturn(null);
		
		// FIXED: Call deleteEmployee instead of newEmployee
		employeeController.deleteEmployee(employee);
		
		verify(employeeView).showError("No existing employee with id 1", employee);
		verifyNoMoreInteractions(ignoreStubs(employeeRepository));
	}
}