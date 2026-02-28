package com.examples.employee.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.mongodb.client.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;

public class EmployeeMongoRepository implements EmployeeRepository {

	private MongoCollection<Document> employeeCollection;

	public EmployeeMongoRepository(com.mongodb.client.MongoClient mongoClient, String dbName, String collectionName) {
		employeeCollection = mongoClient
			.getDatabase(dbName)
			.getCollection(collectionName);
	}

	@Override
	public List<Employee> findAll() {
		return StreamSupport.
			stream(employeeCollection.find().spliterator(), false)
			.map(this::fromDocumentToEmployee)
			.collect(Collectors.toList());
	}

	@Override
	public Employee findById(String id) {
		Document d = employeeCollection.find(Filters.eq("id", id)).first();
		if (d != null) {
			return fromDocumentToEmployee(d);
		}
		return null;
	}

	@Override
	public void save(Employee employee) {
		employeeCollection.insertOne(
			new Document()
				.append("id", employee.getId())
				.append("name", employee.getName()));
	}

	@Override
	public void delete(String id) {
		employeeCollection.deleteOne(Filters.eq("id", id));
	}

	private Employee fromDocumentToEmployee(Document d) {
		return new Employee(d.getString("id"), d.getString("name"));
	}
}