package com.jwt.implementation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwt.implementation.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByUserName(String username);

	@Query("SELECT u FROM User u WHERE u.userName IN :usernames")
	List<User> findByUserNameIn(List<String> usernames);
}
