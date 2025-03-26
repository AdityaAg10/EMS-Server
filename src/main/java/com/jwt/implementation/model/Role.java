package com.jwt.implementation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")  // MongoDB equivalent of @Entity
public class Role {

	@Id
	private String id;  // MongoDB uses String IDs

	@JsonIgnore
	private String role;

	public Role() {
		super();
	}

	public Role(String role) {
		super();
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
