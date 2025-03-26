package com.jwt.implementation.service;

import com.jwt.implementation.model.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    String joinEvent(String eventId, String participant);
    String leaveEvent(String eventId, String participantUsername);

    List<User> getAllUsers();
}
