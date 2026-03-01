package com.examples.employee.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.examples.employee.model.Employee;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class EmployeeMongoRepositoryTestcontainersIT {

	@ClassRule
	public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3");

	private static final String DB_NAME = "employee_db";
	private static final String COLLECTION_NAME = "employees";

	private MongoClient mongoClient;
	private EmployeeMongoRepository employeeRepository;
	private MongoCollection<Document> employeeCollection;

	@Before
	public void setup() {
		mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
		
		employeeRepository = new EmployeeMongoRepository(mongoClient, DB_NAME, COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(DB_NAME);
		database.drop(); // Clean state for each test
		employeeCollection = database.getCollection(COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Test
	public void testFindAll() {
		addTestEmployeeToDatabase("1", "test1");
		addTestEmployeeToDatabase("2", "test2");
		assertThat(employeeRepository.findAll())
			.containsExactly(
				new Employee("1", "test1"),
				new Employee("2", "test2"));
	}

	@Test
	public void testFindById() {
		addTestEmployeeToDatabase("1", "test1");
		addTestEmployeeToDatabase("2", "test2");
		assertThat(employeeRepository.findById("2"))
			.isEqualTo(new Employee("2", "test2"));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(employeeRepository.findById("1"))
			.isNull();
	}

	@Test
	public void testSave() {
		Employee employee = new Employee("1", "test1");
		employeeRepository.save(employee);
		assertThat(readAllEmployeesFromDatabase())
			.containsExactly(employee);
	}

	@Test
	public void testDelete() {
		addTestEmployeeToDatabase("1", "test1");
		employeeRepository.delete("1");
		assertThat(readAllEmployeesFromDatabase()).isEmpty();
	}


	private void addTestEmployeeToDatabase(String id, String name) {
		employeeCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

	private List<Employee> readAllEmployeesFromDatabase() {
		return StreamSupport.stream(employeeCollection.find().spliterator(), false)
				.map(d -> new Employee(d.getString("id"), d.getString("name")))
				.collect(Collectors.toList());
	}
}