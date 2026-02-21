package com.examples.employee.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.examples.employee.model.Employee;
import com.examples.employee.repository.EmployeeRepository;

public class EmployeeMongoRepository implements EmployeeRepository {

	private MongoCollection<Document> employeeCollection;

	public EmployeeMongoRepository(MongoClient client, String dbName, String collectionName) {
		employeeCollection = client
			.getDatabase(dbName)
			.getCollection(collectionName);
	}

	@Override
	public List<Employee> findAll() {
		return StreamSupport.
			stream(employeeCollection.find().spliterator(), false)
			.map(d -> new Employee("" + d.get("id"), "" + d.get("name")))
			.collect(Collectors.toList());
	}
	public Employee findById(String id) {
		Document d = employeeCollection.find(com.mongodb.client.model.Filters.eq("id", id)).first();
		return (d != null) 
				? new Employee(d.getString("id"), d.getString("name")) 
				: null;
		
	}
}