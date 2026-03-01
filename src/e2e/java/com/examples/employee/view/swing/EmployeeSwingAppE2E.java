package com.examples.employee.view.swing;

import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@RunWith(GUITestRunner.class)
public class EmployeeSwingAppE2E extends AssertJSwingJUnitTestCase {

	private static final String MONGO_URL = "mongodb://localhost:27017";
	private static final String DB_NAME = "employee-db";
	private static final String COLLECTION_NAME = "employees";

	private MongoClient mongoClient;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		mongoClient = MongoClients.create(MONGO_URL);
		mongoClient.getDatabase(DB_NAME).drop();

		addTestEmployeeToDatabase("1", "Existing Employee");

		application("com.examples.employee.app.swing.EmployeeSwingApp")
			.withArgs(
				"--mongo-host=localhost",
				"--mongo-port=27017",
				"--db-name=" + DB_NAME,
				"--db-collection=" + COLLECTION_NAME
			)
			.start();

		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Employee View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("employeeList").contents())
			.anySatisfy(e -> assertThat(e).contains("1", "Existing Employee"));
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("New Hire");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("employeeList").contents())
			.anySatisfy(e -> assertThat(e).contains("10", "New Hire"));
	}

	@Test @GUITest
	public void testDeleteButtonSuccess() {
		window.list("employeeList")
			.selectItem(Pattern.compile(".*Existing Employee.*"));
		window.button(JButtonMatcher.withText("Delete Selected")).click();

		assertThat(window.list("employeeList").contents())
			.noneMatch(e -> e.contains("Existing Employee"));
	}

	private void addTestEmployeeToDatabase(String id, String name) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(COLLECTION_NAME)
			.insertOne(new Document().append("id", id).append("name", name));
	}
}