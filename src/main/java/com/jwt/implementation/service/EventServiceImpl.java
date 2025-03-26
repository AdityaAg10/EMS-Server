package com.jwt.implementation.service;

import com.jwt.implementation.DTO.EventDTO;
import com.jwt.implementation.model.Event;
import com.jwt.implementation.model.Role;
import com.jwt.implementation.model.User;
import com.jwt.implementation.repository.EventRepository;
import com.jwt.implementation.repository.RoleRepository;
import com.jwt.implementation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Override
    public Event createEvent(EventDTO eventDTO, String username) {
        User creator = userRepo.findByUserName(username);
        if (creator == null) {
            throw new RuntimeException("User not found");
        }

        // Ensure the creator has the ROLE_HOST
        Role hostRole = roleRepo.findByRole("ROLE_HOST");
        if (hostRole == null) {
            hostRole = new Role("ROLE_HOST");
            roleRepo.save(hostRole);
        }

        if (!creator.getRole().contains(hostRole)) {
            creator.getRole().add(hostRole);
            userRepo.save(creator);  // Ensure the user is saved with a valid ID
        }

        // Collect Hosts (including the creator)
        Set<User> hosts = new HashSet<>();
        hosts.add(creator);

        // Collect Participants
        Set<User> participants = new HashSet<>();
        if (eventDTO.getParticipants() != null) {
            participants = eventDTO.getParticipants().stream()
                    .map(userRepo::findByUserName)
                    .filter(u -> u != null)
                    .collect(Collectors.toSet());
        }

        Event event = new Event(
                eventDTO.getTitle(),
                eventDTO.getDescription(),
                eventDTO.getDate(),
                eventDTO.getLocation(),
                eventDTO.getFee(),
                hosts,
                participants);

        // Save the event first
        Event savedEvent = eventRepo.save(event);

        // Now that the event is saved, update the creator's hostedEvents
        creator.getHostedEvents().add(savedEvent);
        userRepo.save(creator);  // Save the user with the updated hostedEvents

        return savedEvent;
    }

    @Override
    public void deleteEvent(String eventId, String username) {
        Event event = eventRepo.findById(String.valueOf(eventId))
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getRole().equals("ROLE_ADMIN"));

        boolean isHost = event.getHosts().stream()
                .anyMatch(host -> host.getId().equals(user.getId()));

        if (!isAdmin && !isHost) {
            throw new RuntimeException("Access denied: Only a host or admin can delete this event.");
        }

        eventRepo.delete(event);
    }

    @Override
    public Event updateEvent(String eventId, Map<String, Object> updates, String username) {
        Event event = eventRepo.findById(String.valueOf(eventId))
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        boolean isAdmin = isAdmin(user);
        boolean isHost = event.getHosts().contains(user);

        if (!isAdmin && !isHost) {
            throw new RuntimeException("Access denied: Only a host or admin can update this event.");
        }

        // Update only the fields present in the request
        if (updates.containsKey("title")) {
            event.setTitle((String) updates.get("title"));
        }
        if (updates.containsKey("description")) {
            event.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("date")) {
            event.setDate(String.valueOf(LocalDate.parse((String) updates.get("date"))));
        }
        if (updates.containsKey("location")) {
            event.setLocation((String) updates.get("location"));
        }
        if (updates.containsKey("fee")) {
            Object feeValue = updates.get("fee");
            try {
                event.setFee(Double.parseDouble(feeValue.toString())); // Convert any type to Double
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid fee value type: " + feeValue);
            }
        }


        return eventRepo.save(event);
    }

    @Override
    public String addHosts(String eventId, String requestorUsername, String newHostUsername) {
        Optional<Event> optionalEvent = eventRepo.findById(String.valueOf(eventId));
        Optional<User> optionalRequestor = Optional.ofNullable(userRepo.findByUserName(requestorUsername));
        Optional<User> optionalNewHost = Optional.ofNullable(userRepo.findByUserName(newHostUsername));

        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Invalid event");
        }
        if (optionalRequestor.isEmpty()) {
            throw new IllegalArgumentException("Invalid requestor");
        }
        if (optionalNewHost.isEmpty()) {
            throw new IllegalArgumentException("Invalid new host.");
        }

        Event event = optionalEvent.get();
        User requestor = optionalRequestor.get();
        User newHost = optionalNewHost.get();

        // Check if the requestor is an admin or an existing host
        if (!isAdmin(requestor) && !event.getHosts().contains(requestor)) {
            throw new SecurityException("Unauthorized: Only an admin or an existing host can add new hosts.");
        }

        // Fetch the "HOST" role from the database
        Role hostRole = roleRepo.findByRole("ROLE_HOST");
        if (hostRole == null) {
            throw new IllegalStateException("Host role not found in the database.");
        }

        // Check if the new host already has the role
        if (!newHost.getRole().contains(hostRole)) {
            newHost.getRole().add(hostRole);
            userRepo.save(newHost);
        }

        // Add the new host to the event if not already assigned
        if (!event.getHosts().contains(newHost)) {
            event.getHosts().add(newHost);
            eventRepo.save(event);
        } else {
            throw new IllegalStateException("User is already a host of this event.");
        }

        return "Host added successfully.";
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    @Override
    public List<Event> gethostedByMeEvents(String username) {
        User user = userRepo.findByUserName(username); // Fetch User object
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return eventRepo.findByHosts_Id(user.getId()); // Query using the User ID
    }

    @Override
    public List<Event> hostedByOthersEvents(String username) {
        User user = userRepo.findByUserName(username); // Fetch User object
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return eventRepo.findByHosts_IdNot(user.getId()); // Query events not hosted by this user
    }

    @Override
    public Event getEventById(String eventId) {
        return eventRepo.findById(String.valueOf(eventId)).orElse(null);
    }

    @Override
    public String removeHost(String eventId, String requestorUsername, String hostUsername) {
        Optional<Event> optionalEvent = eventRepo.findById(String.valueOf(eventId));
        Optional<User> optionalRequestor = Optional.ofNullable(userRepo.findByUserName(requestorUsername));
        Optional<User> optionalHost = Optional.ofNullable(userRepo.findByUserName(hostUsername));

        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found.");
        }
        if (optionalRequestor.isEmpty()) {
            throw new IllegalArgumentException("Requestor not found.");
        }
        if (optionalHost.isEmpty()) {
            throw new IllegalArgumentException("Host to be removed not found.");
        }

        Event event = optionalEvent.get();
        User requestor = optionalRequestor.get();
        User host = optionalHost.get();

        // Ensure that at least one host remains
        if (event.getHosts().size() <= 1) {
            throw new IllegalStateException("Cannot remove the last host. At least one host must remain.");
        }

        // Check if the requestor is an admin or an existing host
        if (!isAdmin(requestor) && !event.getHosts().contains(requestor)) {
            throw new SecurityException("Unauthorized: Only an admin or an existing host can remove a host.");
        }

        // Check if the user to be removed is actually a host of the event
        if (!event.getHosts().contains(host)) {
            throw new IllegalArgumentException("The specified user is not a host of this event.");
        }

        // Remove the host from the event
        event.getHosts().remove(host);
        eventRepo.save(event);

        // Optionally remove the "ROLE_HOST" if the user is no longer a host of any event
        boolean isStillHost = eventRepo.findAll().stream()
                .anyMatch(e -> e.getHosts().contains(host));

        if (!isStillHost) {
            Role hostRole = roleRepo.findByRole("ROLE_HOST");
            if (hostRole != null) {
                host.getRole().remove(hostRole);
                userRepo.save(host);
            }
        }

        return "Host removed successfully.";
    }

    public String addParticipant(String eventId, String username) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found for ID: " + eventId));

        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        // Check if user is already a participant
        if (event.getParticipants().contains(user)) {
            throw new IllegalStateException("User " + username + " is already a participant of event " + eventId);
        }

        try {
            // Add user to participants
            event.getParticipants().add(user);
            eventRepo.save(event);
            return "Participant added successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Failed to add participant due to an unexpected error: " + e.getMessage(), e);
        }
    }

    public String removeParticipant(String eventId, String username) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        if (event.getParticipants().contains(user)) {
            event.getParticipants().remove(user);
            eventRepo.save(event);
        } else {
            throw new IllegalArgumentException("User is not a participant of this event.");
        }

        return "Participant removed successfully.";
    }

    private boolean isAdmin(User user) {
        return user.getRole().stream().anyMatch(role -> role.getRole().equalsIgnoreCase("ROLE_ADMIN"));
    }

}
