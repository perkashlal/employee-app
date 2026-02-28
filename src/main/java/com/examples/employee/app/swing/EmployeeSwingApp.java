package com.examples.employee.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.examples.employee.controller.EmployeeController;
import com.examples.employee.repository.mongo.EmployeeMongoRepository;
import com.examples.employee.view.swing.EmployeeSwingView;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "EmployeeSwingApp", mixinStandardHelpOptions = true)
public class EmployeeSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String dbName = "employee_db";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "employees";

	public static void main(String[] args) {
		new CommandLine(new EmployeeSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client = MongoClients.create(
						String.format("mongodb://%s:%d", mongoHost, mongoPort));
				
				EmployeeMongoRepository repository = new EmployeeMongoRepository(
						client, dbName, collectionName);
				
				EmployeeSwingView view = new EmployeeSwingView();
				
				EmployeeController controller = new EmployeeController(view, repository);
				view.setEmployeeController(controller);
				
				view.setVisible(true);
				controller.allEmployees();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}
}