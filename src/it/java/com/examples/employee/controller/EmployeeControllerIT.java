package com.examples.employee.controller;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



import com.examples.employee.model.Employee;
import com.examples.employee.repository.mongo.EmployeeMongoRepository;
import com.examples.employee.view.EmployeeView;
import com.mongodb.client.MongoClient; 
import com.mongodb.client.MongoClients;

public class EmployeeControllerIT {

	@Mock
	private EmployeeView employeeView;
	private boolean cleanupDatabase = true;

	private EmployeeMongoRepository employeeRepository;
	private EmployeeController employeeController;
	private AutoCloseable closeable;
	private MongoClient mongoClient; // Variable name is mongoClient

	private static final String DB_NAME = "employee_db";
	private static final String COLLECTION_NAME = "employees";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		mongoClient = MongoClients.create("mongodb://localhost:27017");
		employeeRepository = new EmployeeMongoRepository(mongoClient, DB_NAME, COLLECTION_NAME);
		
		if (cleanupDatabase) {
			for (Employee employee : employeeRepository.findAll()) {
				employeeRepository.delete(employee.getId());
			}
		}
		
		employeeController = new EmployeeController(employeeView, employeeRepository);
	}

	@After
	public void tearDown() throws Exception {
		if (mongoClient != null) {
			mongoClient.close();
		}
		closeable.close();
	
	}

	@Test
	public void testAllEmployees() {
		Employee employee = new Employee("1", "test1");
		employeeRepository.save(employee);
		employeeController.allEmployees();
		verify(employeeView).showAllEmployees(asList(employee));
	}
}