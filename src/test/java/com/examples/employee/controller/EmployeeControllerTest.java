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
import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;

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
	@Test
	public void testNewEmployeeWhenEmployeeAlreadyExists() {
	    Employee employeeToAdd = new Employee("1", "test");
	    Employee existingEmployee = new Employee("1", "name");
	    
	    when(employeeRepository.findById("1")).thenReturn(existingEmployee);
	    
	    employeeController.newEmployee(employeeToAdd);
	    
	    verify(employeeView)
	        .showError("Already existing employee with id 1", existingEmployee);
	    verifyNoMoreInteractions(ignoreStubs(employeeRepository));
	}
	@Test
	void testDeleteEmployeeWhenEmployeeExists() {
	    Employee employeeToDelete = new Employee("1", "test");
	    when(employeeRepository.findById("1")).thenReturn(employeeToDelete);
	    
	    employeeController.deleteEmployee(employeeToDelete);
	    
	    InOrder inOrder = inOrder(employeeRepository, employeeView);
	    inOrder.verify(employeeRepository).delete("1");
	    inOrder.verify(employeeView).employeeRemoved(employeeToDelete);
	}
	@Test
	void testDeleteEmployeeWhenEmployeeDoesNotExist() {
	    Employee employee = new Employee("1", "test");
	    when(employeeRepository.findById("1")).thenReturn(null);
	    
	    employeeController.deleteEmployee(employee);
	    
	    verify(employeeView)
	        .showError("No existing employee with id 1", employee);
	    verifyNoMoreInteractions(ignoreStubs(employeeRepository));
	}
}