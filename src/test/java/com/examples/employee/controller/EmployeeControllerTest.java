package com.examples.employee.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InOrder;

import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;
import com.examples.employee.view.EmployeeView;

public class EmployeeControllerTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private EmployeeView employeeView;

	@InjectMocks
	private EmployeeController employeeController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllEmployees() {
		Employee employee = new Employee("1", "test");
		List<Employee> employees = asList(employee);
		when(employeeRepository.findAll()).thenReturn(employees);
		
		employeeController.allEmployees();
		
		assertThat(employees.get(0).getId()).isEqualTo("1");
		assertThat(employees.get(0).getName()).isEqualTo("test");
		
		verify(employeeView).showAllEmployees(employees);
	}
	
	@Test
	public void testNewEmployeeWhenEmployeeDoesNotAlreadyExist() {
		Employee employee = new Employee("1", "test");
		when(employeeRepository.findById("1")).thenReturn(null);
		
		employeeController.newEmployee(employee);
		
		assertThat(employee.getId()).isEqualTo("1");
		assertThat(employee.getName()).isEqualTo("test");
		
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
		
		employeeController.deleteEmployee(employee);
		
		verify(employeeView).showError("No existing employee with id 1", employee);
		verifyNoMoreInteractions(ignoreStubs(employeeRepository));
	}

	@Test
	public void testEmployeeModelMethodsForCoverage() {
		Employee e1 = new Employee("1", "test");
		Employee e2 = new Employee("1", "test");
		Employee e3 = new Employee("2", "other");
		
		assertThat(e1).isEqualTo(e2);
		assertThat(e1).isNotEqualTo(e3);
		assertThat(e1).isNotEqualTo(null);
		assertThat(e1).isNotEqualTo("string");
		assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
		assertThat(e1.hashCode()).isNotEqualTo(e3.hashCode());
	}
}