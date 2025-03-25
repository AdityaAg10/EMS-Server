package com.jwt.implementation.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwt.implementation.model.Role;
import com.jwt.implementation.model.User;
import com.jwt.implementation.DTO.UserDTO;
import com.jwt.implementation.repository.RoleRepository;
import com.jwt.implementation.repository.UserRepository;

@Service
public class DefaultUserServiceImpl implements DefaultUserService{

	@Autowired
	UserRepository userRepo;

	@Autowired
	RoleRepository roleRepo;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUserName(username);
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), mapRolesToAuthorities(user.getRole()));
	}

	public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	}


	@Override
	public User save(UserDTO userRegisteredDTO) {
		// Check if the user already exists
		if (userRepo.findByUserName(userRegisteredDTO.getUserName()) != null) {
			throw new IllegalArgumentException("User already exists with username: " + userRegisteredDTO.getUserName());
		}

		// Validate role input
		String roleName = userRegisteredDTO.getRole().equalsIgnoreCase("ADMIN") ? "ROLE_ADMIN" : "ROLE_USER";

		// Find existing role or create a new one
		Role role = roleRepo.findByRole(roleName);
		if (role == null) {
			role = new Role(roleName);
			role = roleRepo.save(role);  // Save role and persist it
		}

		// Create and save the user
		User user = new User();
		user.setEmail(userRegisteredDTO.getEmail());
		user.setUserName(userRegisteredDTO.getUserName());
		user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
		user.setRole(role);  // Assign the role

		return userRepo.save(user);
	}



}