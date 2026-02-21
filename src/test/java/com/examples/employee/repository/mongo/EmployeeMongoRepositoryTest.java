package com.examples.employee.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.InetSocketAddress;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mongodb.client.MongoClient;
import com.examples.employee.model.Employee;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

class EmployeeMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private EmployeeMongoRepository employeeRepository;
	private MongoCollection<Document> employeeCollection;

	private static final String DB_NAME = "employee-db";
	private static final String COLLECTION_NAME = "employee-collection";

	@BeforeAll
	static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterAll
	static void shutdownServer() {
		server.shutdown();
	}

	@BeforeEach
	void setup() {
		// Use the modern MongoClients.create for JUnit 5
		client = MongoClients.create("mongodb://" + serverAddress.getHostName() + ":" + serverAddress.getPort());
		employeeRepository = new EmployeeMongoRepository(client, DB_NAME, COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		employeeCollection = database.getCollection(COLLECTION_NAME);
	}

	@AfterEach
	void tearDown() {
		client.close();
	}

	@Test
	void testFindAllWhenDatabaseIsEmpty() {
		assertThat(employeeRepository.findAll()).isEmpty();
	}
	@Test
	void testFindAllWhenDatabaseIsNotEmpty() {
		addTestEmployeeToDatabase("101", "Kanchan");
		addTestEmployeeToDatabase("102", "Parkash");
		
		assertThat(employeeRepository.findAll())
			.containsExactly(
				new Employee("101", "Kanchan"),
				new Employee("102", "Parkash"));
	}
	@Test
	void testEmployeeMethodsForCoverage() {
		Employee sunil = new Employee("1", "Sunil");
		Employee sunilCopy = new Employee("1", "Sunil");
		Employee mayoor = new Employee("2", "Mayoor");

		assertThat(sunil.getId()).isEqualTo("1");
		assertThat(sunil.getName()).isEqualTo("Sunil");
	
		assertThat(sunil.hashCode()).isEqualTo(sunilCopy.hashCode());
		assertThat(sunil).isEqualTo(sunilCopy);
		assertThat(sunil).isNotEqualTo(null);
		assertThat(sunil).isNotEqualTo("Not an Employee");
		
		Employee sameIdDifferentName = new Employee("1", "Mayoor");
		Employee differentIdSameName = new Employee("2", "Sunil");
		assertThat(sunil).isNotEqualTo(sameIdDifferentName);
		assertThat(sunil).isNotEqualTo(differentIdSameName);
		assertThat(sunil).isNotEqualTo(mayoor);
	}
	@Test
	public void testFindByIdNotFound() {
		Employee found = employeeRepository.findById("1");
	    assertThat(found).isNull();
	}
	@Test
	public void testFindByIdFound() {
		addTestEmployeeToDatabase("1", "Mayoor");
		addTestEmployeeToDatabase("2", "Sunil");
		Employee found = employeeRepository.findById("2");
		assertThat(found).isEqualTo(new Employee("2", "Sunil"));
	}
	private void addTestEmployeeToDatabase(String id, String name) {
		employeeCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}
}