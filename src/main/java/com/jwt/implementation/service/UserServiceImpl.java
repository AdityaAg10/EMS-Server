package com.jwt.implementation.service;

import com.jwt.implementation.model.Event;
import com.jwt.implementation.model.Expense;
import com.jwt.implementation.model.ExpenseItem;
import com.jwt.implementation.model.User;
import com.jwt.implementation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private ExpenseItemRepository expenseItemRepo;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public String joinEvent(String eventId, String participantUsername) {
        Optional<Event> optionalEvent = eventRepo.findById(eventId);
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByUserName(participantUsername));

        if (optionalEvent.isEmpty()) {
            return "Event doesn't exist";
        }

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        Event event = optionalEvent.get();
        User participant = optionalUser.get();

        // Check if the user is already a participant
        if (event.getParticipants().contains(participant)) {
            return "User is already a participant in this event";
        }

        // Add participant
        event.getParticipants().add(participant);
        eventRepo.save(event); // Save the updated event

        return "Joined the event successfully";
    }

    @Override
    public String leaveEvent(String eventId, String participantUsername) {
        Optional<Event> optionalEvent = eventRepo.findById(eventId);
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByUserName(participantUsername));

        if (optionalEvent.isEmpty()) {
            return "Event doesn't exist";
        }

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        Event event = optionalEvent.get();
        User participant = optionalUser.get();

        // Debugging logs to check participant and event participants
        System.out.println("Checking if user exists in event...");
        System.out.println("Participant ID: " + participant.getId());
        System.out.println("Event Participants: " + event.getParticipants());

        for (User u : event.getParticipants()) {
            System.out.println("Stored User ID: " + u.getId());
        }

        if (!event.getParticipants().contains(participant)) {
            return "User is not a participant in this event";
        }

        // Remove participant
        event.getParticipants().remove(participant);
        eventRepo.save(event); // Save the updated event

        return "Left the event successfully";
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.findByUserName(username);
    }

    @Override
    public User getUserById(String userId) {
        Optional<User> user = userRepo.findById(userId);
        return user.orElse(null);
    }

    @Override
    public User updateUser(String username, User updatedUser) {
        User existingUser = userRepo.findByUserName(username);
        if (existingUser == null) {
            throw new RuntimeException("User not found"); // or return ResponseEntity with an error
        }

        // Only update fields if they are non-null and not empty
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getUserName() != null && !updatedUser.getUserName().trim().isEmpty()) {
            existingUser.setUserName(updatedUser.getUserName());
        }

        return userRepo.save(existingUser);
    }


    @Override
    public String updatePassword(String username, String oldPassword, String newPassword) {
        User existingUser = userRepo.findByUserName(username);
        if (existingUser != null) {
            if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                return "Incorrect old password";
            }
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(existingUser);
            return "Password updated successfully";
        }
        return "User not found";
    }

    @Override
    public String deleteUser(String userId) {
        Optional<User> optionalUser = userRepo.findById(userId);

        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();

        // Remove user from events they participated in
        List<Event> allEvents = eventRepo.findAll();
        for (Event event : allEvents) {
            if (event.getParticipants().contains(user)) {
                event.getParticipants().remove(user);
                eventRepo.save(event);
            }

            // Remove user from hosts if they are one of the event hosts
            if (event.getHosts().contains(user)) {
                event.getHosts().remove(user);

                // If no hosts remain, delete the event and related expenses
                if (event.getHosts().isEmpty()) {
                    deleteEventAndExpenses(event);
                } else {
                    eventRepo.save(event);
                }
            }
        }

        // Delete the user
        userRepo.delete(user);

        return "User removed from events, removed as host from hosted events (deleted if no hosts remained), and user account deleted successfully.";
    }

    /**
     * Deletes an event and all related expenses and expense items.
     */
    private void deleteEventAndExpenses(Event event) {
        // Find and delete all expenses associated with this event
        List<Expense> eventExpenses = expenseRepo.findByEventId(event.getId());

        for (Expense expense : eventExpenses) {
            // Delete all expense items related to the expense
            for (ExpenseItem item : expense.getItems()) {
                expenseItemRepo.delete(item);
            }
            // Delete the expense itself
            expenseRepo.delete(expense);
        }

        // Finally, delete the event
        eventRepo.delete(event);
    }



}
