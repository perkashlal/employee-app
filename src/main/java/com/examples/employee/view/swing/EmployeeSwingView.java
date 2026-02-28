package com.examples.employee.view.swing;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import com.examples.employee.controller.EmployeeController;
import com.examples.employee.model.Employee;
import com.examples.employee.view.EmployeeView;

public class EmployeeSwingView extends JFrame implements EmployeeView {

	private static final long serialVersionUID = 1L;
	private JTextField idTextBox;
	private JTextField nameTextBox;
	private JButton btnAdd;
	private JList<Employee> employeeList;
	private DefaultListModel<Employee> listEmployeesModel;
	private JButton btnDelete;
	private JLabel errorMessageLabel;
	private transient EmployeeController employeeController;

	public EmployeeSwingView() {
		setTitle("Employee View");
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
		btnAdd.setName("btnAdd");
		btnAdd.setEnabled(false);
		add(btnAdd);

		listEmployeesModel = new DefaultListModel<>();
		employeeList = new JList<>(listEmployeesModel);
		employeeList.setName("employeeList");
		
		employeeList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Employee emp = (Employee) value;
				return super.getListCellRendererComponent(list, 
					emp != null ? emp.getId() + " - " + emp.getName() : "", 
					index, isSelected, cellHasFocus);
			}
		});
		add(new JScrollPane(employeeList));

		btnDelete = new JButton("Delete Selected");
		btnDelete.setName("btnDelete");
		btnDelete.setEnabled(false);
		add(btnDelete);

		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setName("errorMessageLabel");
		add(errorMessageLabel);

		java.awt.event.KeyAdapter btnEnabler = new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				btnAdd.setEnabled(!idTextBox.getText().trim().isEmpty() && !nameTextBox.getText().trim().isEmpty());
			}
		};
		idTextBox.addKeyListener(btnEnabler);
		nameTextBox.addKeyListener(btnEnabler);

		btnAdd.addActionListener(e -> employeeController.newEmployee(new Employee(idTextBox.getText(), nameTextBox.getText())));
		btnDelete.addActionListener(e -> employeeController.deleteEmployee(employeeList.getSelectedValue()));
		employeeList.addListSelectionListener(e -> btnDelete.setEnabled(employeeList.getSelectedIndex() != -1));
		
		pack();
	}

	// This is the method your test is looking for
	public DefaultListModel<Employee> getListEmployeesModel() {
		return listEmployeesModel;
	}

	public void setEmployeeController(EmployeeController employeeController) {
		this.employeeController = employeeController;
	}

	@Override
	public void showAllEmployees(List<Employee> employees) {
		listEmployeesModel.clear();
		employees.forEach(listEmployeesModel::addElement);
	}

	@Override
	public void employeeAdded(Employee employee) {
		listEmployeesModel.addElement(employee);
		resetErrorLabel();
	}

	@Override
	public void employeeRemoved(Employee employee) {
		listEmployeesModel.removeElement(employee);
		resetErrorLabel();
	}

	@Override
	public void showError(String message, Employee employee) {
		errorMessageLabel.setText(message + ": " + employee.getId() + " - " + employee.getName());
	}

	@Override
	public void showErrorEmployeeNotFound(String message, Employee employee) {
		errorMessageLabel.setText(message + ": " + employee.getId() + " - " + employee.getName());
		listEmployeesModel.removeElement(employee);
	}

	private void resetErrorLabel() {
		errorMessageLabel.setText(" ");
	}
}