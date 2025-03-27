package com.jwt.implementation.controller;

import com.jwt.implementation.model.User;
import com.jwt.implementation.repository.UserRepository;
import com.jwt.implementation.service.EventService;
import com.jwt.implementation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;


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

//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepo.findAll();

            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No users found.");
            }

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/update")
    public User updateUser(@RequestBody User updatedUser) {
        System.out.println(updatedUser);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.updateUser(username, updatedUser);
    }

    @PutMapping("/updatePassword")
    public String updatePassword(@RequestBody Map<String, String> passwordData) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.updatePassword(username, passwordData.get("oldPassword"), passwordData.get("newPassword"));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            String result = userService.deleteUser(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }



}
