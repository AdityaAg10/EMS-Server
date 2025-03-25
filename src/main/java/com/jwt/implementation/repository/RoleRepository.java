package com.jwt.implementation.repository;

import com.jwt.implementation.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
	Role findByRole(String role);
}
