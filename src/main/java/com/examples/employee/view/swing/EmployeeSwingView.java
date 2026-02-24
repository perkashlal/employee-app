package com.examples.employee.view.swing;

import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import com.examples.employee.controller.EmployeeController;
import com.examples.employee.model.Employee;

public class EmployeeSwingView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField idTextBox;
	private JTextField nameTextBox;
	private JButton btnAdd;
	private JList<String> employeeList;
	private JButton btnDelete;
	private DefaultListModel<String> listEmployeesModel;
	private JLabel errorMessageLabel;
	

	public EmployeeSwingView() {
		setLayout(new FlowLayout());

		add(new JLabel("id"));
		idTextBox = new JTextField(10);
		idTextBox.setName("idTextBox");
		add(idTextBox);

		add(new JLabel("name"));
		nameTextBox = new JTextField(10);
		nameTextBox.setName("nameTextBox");
		add(nameTextBox);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		add(btnAdd);

		listEmployeesModel = new DefaultListModel<>();
		employeeList = new JList<>(listEmployeesModel);
		employeeList.setName("employeeList");
		add(employeeList);

		employeeList.addListSelectionListener(e -> {
			btnDelete.setEnabled(employeeList.getSelectedIndex() != -1);
		});

		btnDelete = new JButton("Delete Selected");
		btnDelete.setEnabled(false);
		add(btnDelete);

		errorMessageLabel = new JLabel("");
		errorMessageLabel.setName("errorMessageLabel");
		add(errorMessageLabel);

		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAdd.setEnabled(
					!idTextBox.getText().trim().isEmpty() &&
					!nameTextBox.getText().trim().isEmpty()
				);
			}
		};

		idTextBox.addKeyListener(btnAddEnabler);
		nameTextBox.addKeyListener(btnAddEnabler);
	}

	public void showAllEmployees(List<Employee> employees) {
		listEmployeesModel.clear();
		employees.stream().forEach(employee -> listEmployeesModel.addElement(employee.getId() + " - " + employee.getName()));
	}

	public void showError(String message, Employee employee) {
		errorMessageLabel.setText(message + ": " + employee.getId() + " - " + employee.getName());
	}

	public void showErrorEmployeeNotFound(String message, Employee employee) {
	}

	public void setEmployeeController(EmployeeController employeeController) {
	}

	public DefaultListModel<String> getListEmployeesModel() {
		return listEmployeesModel;
	}
}