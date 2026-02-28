package com.examples.employee.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.examples.employee.controller.EmployeeController;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.mongo.EmployeeMongoRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@RunWith(GUITestRunner.class)
public class EmployeeSwingViewIT extends AssertJSwingJUnitTestCase {

	@ClassRule
	public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3");

	private MongoClient mongoClient;
	private FrameFixture window;
	private EmployeeSwingView employeeSwingView;
	private EmployeeController employeeController;
	private EmployeeMongoRepository employeeRepository;

	@Override
	protected void onSetUp() {
		mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
		employeeRepository = new EmployeeMongoRepository(mongoClient, "employee_db", "employees");
		
		for (Employee employee : employeeRepository.findAll()) {
			employeeRepository.delete(employee.getId());
		}

		GuiActionRunner.execute(() -> {
			employeeSwingView = new EmployeeSwingView();
			employeeController = new EmployeeController(employeeSwingView, employeeRepository);
			employeeSwingView.setEmployeeController(employeeController);
			return employeeSwingView;
		});

		window = new FrameFixture(robot(), employeeSwingView);
		window.show(); 
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	@Test @GUITest
	public void testAllEmployees() {
		employeeRepository.save(new Employee("1", "lal"));
		employeeRepository.save(new Employee("2", "perkash"));
		
		GuiActionRunner.execute(() -> employeeController.allEmployees());
		
		assertThat(window.list().contents())
			.containsExactly("1 - lal", "2 - perkash");
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("lal");
		window.button("btnAdd").click(); 
		assertThat(window.list().contents()).containsExactly("1 - lal");
	}

	@Test @GUITest
	public void testDeleteButtonSuccess() {
		GuiActionRunner.execute(() -> employeeController.newEmployee(new Employee("1", "lal")));
		window.list().selectItem(0);
		window.button("btnDelete").click(); 
		assertThat(window.list().contents()).isEmpty();
	}

	@Test @GUITest
	public void testDeleteButtonError() {
		Employee employee = new Employee("1", "kanchan");
		GuiActionRunner.execute(() -> employeeSwingView.getListEmployeesModel().addElement(employee));
		
		window.list().selectItem(0);
		window.button("btnDelete").click(); 
		
		assertThat(window.list().contents()).containsExactly("1 - kanchan");
		window.label("errorMessageLabel")
			.requireText("No existing employee with id 1: 1 - kanchan");
	}
}