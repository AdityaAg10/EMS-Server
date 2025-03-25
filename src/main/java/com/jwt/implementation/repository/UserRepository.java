package com.jwt.implementation.repository;

import com.jwt.implementation.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
	User findByUserName(String username);

	@Query("{ 'userName' : { $in: ?0 } }")
	List<User> findByUserNameIn(List<String> usernames);
}
