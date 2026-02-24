package com.examples.employee.view.swing;

import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import com.examples.employee.controller.EmployeeController;

public class EmployeeSwingView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField idTextBox;
	private JTextField nameTextBox;
	private JButton btnAdd;

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

		JList<String> employeeList = new JList<>(new DefaultListModel<>());
		employeeList.setName("employeeList");
		add(employeeList);

		JButton btnDelete = new JButton("Delete Selected");
		btnDelete.setEnabled(false);
		add(btnDelete);

		JLabel errorMessageLabel = new JLabel("");
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

	public void setEmployeeController(EmployeeController employeeController) {
	}
}