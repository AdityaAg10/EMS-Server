package com.jwt.implementation.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;



@Entity
@Table(name= "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENT
	private Long id;

	private String userName;
	private String password;
	private String email;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_role", joinColumns = @JoinColumn(name = "cust_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id") )
	Set<Role> roles = new HashSet<Role>();

	@JsonBackReference
	@JsonIgnore // prevents from reaching infinite recursion when being called in both event and user
	@ManyToMany(mappedBy = "hosts", fetch = FetchType.LAZY)
	private Set<Event> hostedEvents = new HashSet<>();



	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Event> getHostedEvents() {
		return hostedEvents;
	}

	public void setHostedEvents(Set<Event> hostedEvents) {
		this.hostedEvents = hostedEvents;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
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



}