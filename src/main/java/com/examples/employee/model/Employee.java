package com.examples.employee.model;

import java.util.Objects;

public class Employee {

	private String id;
	private String name;

	public Employee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Employee) {
			Employee other = (Employee) obj;
			return Objects.equals(id, other.id) && 
				   Objects.equals(name, other.name);
		}
		return false;
	}
}