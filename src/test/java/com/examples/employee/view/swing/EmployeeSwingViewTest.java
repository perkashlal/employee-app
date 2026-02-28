package com.examples.employee.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.employee.controller.EmployeeController;
import com.examples.employee.model.Employee;

@RunWith(GUITestRunner.class)
public class EmployeeSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private EmployeeSwingView employeeSwingView;

	@Mock
	private EmployeeController employeeController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			employeeSwingView = new EmployeeSwingView();
			employeeSwingView.setEmployeeController(employeeController);
			return employeeSwingView;
		});
		window = new FrameFixture(robot(), employeeSwingView);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}


	@Test @GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("idTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("name"));
		window.textBox("nameTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("employeeList");
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}


	@Test
	public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	public void testAddButtonShouldBeDisabledWhenIdIsBlank() {
		window.textBox("idTextBox").enterText(" ");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	public void testAddButtonShouldBeDisabledWhenNameIsBlank() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenAnEmployeeIsSelected() {
		GuiActionRunner.execute(() -> employeeSwingView.getListEmployeesModel().addElement(new Employee("1", "test")));
		window.list("employeeList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).requireEnabled();
		window.list("employeeList").clearSelection();
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
	}


	@Test
	public void testsShowAllEmployeesShouldAddEmployeeDescriptionsToTheList() {
		Employee employee1 = new Employee("1", "test1");
		Employee employee2 = new Employee("2", "test2");
		GuiActionRunner.execute(() -> employeeSwingView.showAllEmployees(Arrays.asList(employee1, employee2)));
		assertThat(window.list().contents()).containsExactly("1 - test1", "2 - test2");
	}

	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Employee employee = new Employee("1", "test1");
		GuiActionRunner.execute(() -> employeeSwingView.showError("error message", employee));
		window.label("errorMessageLabel").requireText("error message: 1 - test1");
	}

	@Test
	public void testEmployeeAddedShouldAddTheEmployeeToTheListAndResetTheErrorLabel() {
		Employee employee = new Employee("1", "test1");
		
		GuiActionRunner.execute(() -> employeeSwingView.employeeAdded(employee));
		
		assertThat(window.list().contents()).containsExactly("1 - test1");
		
	window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testEmployeeRemovedShouldRemoveTheEmployeeFromTheListAndResetTheErrorLabel() {
		Employee employee1 = new Employee("1", "test1");
		Employee employee2 = new Employee("2", "test2");
		GuiActionRunner.execute(() -> {
			employeeSwingView.getListEmployeesModel().addElement(employee1);
			employeeSwingView.getListEmployeesModel().addElement(employee2);
		});
		GuiActionRunner.execute(() -> employeeSwingView.employeeRemoved(new Employee("1", "test1")));
		assertThat(window.list().contents()).containsExactly("2 - test2");
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testShowErrorEmployeeNotFound() {
		Employee employee1 = new Employee("1", "test1");
		GuiActionRunner.execute(() -> employeeSwingView.getListEmployeesModel().addElement(employee1));
		GuiActionRunner.execute(() -> employeeSwingView.showErrorEmployeeNotFound("error message", employee1));
		window.label("errorMessageLabel").requireText("error message: 1 - test1");
		assertThat(window.list().contents()).isEmpty();
	}

	
	@Test
	public void testAddButtonShouldDelegateToEmployeeControllerNewEmployee() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		verify(employeeController).newEmployee(new Employee("1", "test"));
	}

	@Test
	public void testDeleteButtonShouldDelegateToEmployeeControllerDeleteEmployee() {
		Employee employee1 = new Employee("1", "test1");
		GuiActionRunner.execute(() -> employeeSwingView.getListEmployeesModel().addElement(employee1));
		window.list("employeeList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		verify(employeeController).deleteEmployee(employee1);
	}
	@Test
	public void testEmployeeEntityMethodsForCoverage() {
	    Employee employee = new Employee();
	    employee.setId("99");
	    employee.setName("CoverUser");
	    
	    assertThat(employee.getId()).isEqualTo("99");
	    assertThat(employee.getName()).isEqualTo("CoverUser");
	    assertThat(employee.toString()).isEqualTo("Employee [id=99, name=CoverUser]");
	}
	@Test
	public void testListCellRendererHandleNullValue() {
	    GuiActionRunner.execute(() -> {
	        DefaultListModel<Employee> model = employeeSwingView.getListEmployeesModel();
	        model.addElement(null); 
	    });
	    assertThat(window.list().contents()).contains("");
	}
}