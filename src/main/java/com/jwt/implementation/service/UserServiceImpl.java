package com.jwt.implementation.service;

import com.jwt.implementation.model.Event;
import com.jwt.implementation.model.User;
import com.jwt.implementation.repository.EventRepository;
import com.jwt.implementation.repository.RoleRepository;
import com.jwt.implementation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
