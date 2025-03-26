package com.jwt.implementation.controller;

import com.jwt.implementation.model.User;
import com.jwt.implementation.service.EventService;
import com.jwt.implementation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @PutMapping("/joinEvent/{eventId}")
    public String addParticipant(@PathVariable String eventId){
        String participant = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.joinEvent(eventId, participant);
    }

    @PutMapping("/leaveEvent/{eventId}")
    public String leaveEvent(@PathVariable String eventId){
        String participant = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.leaveEvent(eventId, participant);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
