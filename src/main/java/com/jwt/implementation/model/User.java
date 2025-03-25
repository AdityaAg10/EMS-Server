package com.jwt.implementation.model;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "users") // MongoDB equivalent of @Entity
public class User {

	@Id
	private String id; // MongoDB ObjectId as String

	private String userName;
	private String password;
	private String email;

	@DBRef
	private Set<Role> roles = new HashSet<>();

	@JsonBackReference
	@JsonIgnore
	@DBRef
	private Set<Event> hostedEvents = new HashSet<>();

	// ✅ Keeping all functions unchanged

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Event> getHostedEvents() {
		return hostedEvents;
	}

	public void setHostedEvents(Set<Event> hostedEvents) {
		this.hostedEvents = hostedEvents;
	}

	public void setId(String id) { // Changed from String to String
		this.id = id;
	}

	public String getId() { // Changed return type from String to String
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Role> getRole() {
		return roles;
	}

	public void setRole(Role role) {
		this.roles.add(role);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id); // Compare by ID, not memory reference
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
