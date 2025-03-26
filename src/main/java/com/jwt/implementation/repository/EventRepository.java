package com.jwt.implementation.repository;

import com.jwt.implementation.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByHosts_Id(String hostId);

    @Query("{ 'hosts': { $nin: [?0] } }")
    List<Event> findByHosts_IdNot(String hostId);

}
