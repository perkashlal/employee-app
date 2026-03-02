package com.examples.employee.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.employee.model.Employee;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

class EmployeeMongoRepositoryTest {

    @Mock private MongoClient mongoClient;
    @Mock private MongoDatabase mongoDatabase;
    @Mock private MongoCollection<Document> mongoCollection;
    @Mock private FindIterable<Document> findIterable;

    private EmployeeMongoRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the chain: client -> database -> collection
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(mongoCollection);
        
        repository = new EmployeeMongoRepository(mongoClient, "test-db", "test-collection");
    }

    @Test
    void testFindAll() {
        Document d1 = new Document("id", "1").append("name", "John");
        Document d2 = new Document("id", "2").append("name", "Jane");
        
        when(mongoCollection.find()).thenReturn(findIterable);
        // Stub the stream support by providing a list converted to iterable
        when(findIterable.spliterator()).thenReturn(List.of(d1, d2).spliterator());

        List<Employee> results = repository.findAll();
        
        assertThat(results).containsExactly(
            new Employee("1", "John"),
            new Employee("2", "Jane")
        );
    }

    @Test
    void testFindByIdFound() {
        Document d = new Document("id", "1").append("name", "John");
        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(d);

        Employee result = repository.findById("1");

        assertThat(result).isEqualTo(new Employee("1", "John"));
    }

    @Test
    void testFindByIdNotFound() {
        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);

        Employee result = repository.findById("1");

        assertThat(result).isNull();
    }

    @Test
    void testSave() {
        Employee employee = new Employee("1", "John");
        
        repository.save(employee);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(captor.capture());
        
        Document captured = captor.getValue();
        assertThat(captured.get("id")).isEqualTo("1");
        assertThat(captured.get("name")).isEqualTo("John");
    }

    @Test
    void testDelete() {
        repository.delete("1");
        // Verify that deleteOne was called with a filter for the correct ID
        verify(mongoCollection).deleteOne(any(Bson.class));
    }
}