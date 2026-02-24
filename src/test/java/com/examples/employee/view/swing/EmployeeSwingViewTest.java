package com.examples.employee.view.swing;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.examples.employee.controller.EmployeeController;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.fixture.JButtonFixture;
import java.util.List;
import static org.mockito.Mockito.verify;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import com.examples.employee.model.Employee;
import static org.assertj.core.api.Assertions.assertThat;




@RunWith(GUITestRunner.class)
public class EmployeeSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private EmployeeSwingView employeeSwingView;
    private DefaultListModel<String> listEmployeesModel;
	private JLabel errorMessageLabel;

    @Mock
    private EmployeeController employeeController;

    private AutoCloseable closeable;

    @Override
    protected void onSetUp() {
        closeable = MockitoAnnotations.openMocks(this); 
        
        GuiActionRunner.execute(() -> {
            employeeSwingView = new EmployeeSwingView();
            employeeSwingView.setEmployeeController(employeeController); 
            
            listEmployeesModel = employeeSwingView.getListEmployeesModel(); 
            return employeeSwingView;
        });
        window = new FrameFixture(robot(), employeeSwingView);
        window.show();
    }
    

    @Override
	protected void onTearDown() {
		if (closeable != null) { 
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
    }

    @Test
    public void testControlsInitialStates() {
        window.label(JLabelMatcher.withText("id"));
        window.textBox("idTextBox").requireEnabled();
        window.label(JLabelMatcher.withText("name"));
        window.textBox("nameTextBox").requireEnabled();
        window.button(JButtonMatcher.withText("Add")).requireDisabled();
        window.list("employeeList");
        window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
        
        window.label("errorMessageLabel").requireText(""); 
    }
    @Test
    public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireEnabled();
    }
    @Test
	public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture idTextBox = window.textBox("idTextBox");
		JTextComponentFixture nameTextBox = window.textBox("nameTextBox");

		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		idTextBox.setText("");
		nameTextBox.setText("");

		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}
    @Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenAnEmployeeIsSelected() {
		GuiActionRunner.execute(() -> 
			employeeSwingView.getListEmployeesModel().addElement("1 - John Doe")
		);
		window.list("employeeList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();
		window.list("employeeList").clearSelection();
		deleteButton.requireDisabled();
	}
    @Test
	public void testsShowAllEmployeesShouldAddEmployeeDescriptionsToTheList() {
		Employee employee1 = new Employee("1", "test1");
		Employee employee2 = new Employee("2", "test2");
		
		GuiActionRunner.execute(
			() -> employeeSwingView.showAllEmployees(
					java.util.Arrays.asList(employee1, employee2))
		);
		
		String[] listContents = window.list("employeeList").contents();
		assertThat(listContents)
			.containsExactly("1 - test1", "2 - test2");
	}
    @Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Employee employee = new Employee("1", "test1");
		GuiActionRunner.execute(
			() -> employeeSwingView.showError("error message", employee)
		);
		window.label("errorMessageLabel")
			.requireText("error message: 1 - test1");
	}
    @Test
	public void testShowErrorEmployeeNotFound() {
		Employee employee1 = new Employee("1", "test1");
		Employee employee2 = new Employee("2", "test2");
		
		GuiActionRunner.execute(() -> {
			listEmployeesModel.addElement("1 - test1");
			listEmployeesModel.addElement("2 - test2");
		});

		GuiActionRunner.execute(
			() -> employeeSwingView.showErrorEmployeeNotFound("error message", employee1)
		);

		window.label("errorMessageLabel")
			.requireText("error message: 1 - test1");
		
		assertThat(window.list("employeeList").contents())
			.containsExactly("2 - test2");
	}
    @Test
    public void testEmployeeAddedShouldAddTheEmployeeToTheListAndResetTheErrorLabel() {
        Employee employee = new Employee("1", "test1");
        GuiActionRunner.execute(() -> {
            employeeSwingView.showError("any error", new Employee("2", "test2"));
            employeeSwingView.employeeAdded(employee);
        });
        String[] listContents = window.list("employeeList").contents();
        assertThat(listContents).containsExactly("1 - test1");
        window.label("errorMessageLabel").requireText("");
    }
    @Test
    public void testEmployeeRemovedShouldRemoveTheEmployeeFromTheListAndResetTheErrorLabel() {
        Employee employee1 = new Employee("1", "test1");
        Employee employee2 = new Employee("2", "test2");
        GuiActionRunner.execute(() -> {
            listEmployeesModel.addElement("1 - test1");
            listEmployeesModel.addElement("2 - test2");
        });
        
        GuiActionRunner.execute(
            () -> employeeSwingView.employeeRemoved(new Employee("1", "test1"))
        );
        
        String[] listContents = window.list("employeeList").contents();
        assertThat(listContents).containsExactly("2 - test2");
        window.label("errorMessageLabel").requireText("");
    }
    @Test
    public void testAddButtonShouldDelegateToEmployeeControllerNewEmployee() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        
        verify(employeeController).newEmployee(new Employee("1", "test"));
    }
}