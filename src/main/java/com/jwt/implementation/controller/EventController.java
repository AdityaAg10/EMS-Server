package com.jwt.implementation.controller;

import com.jwt.implementation.DTO.EventDTO;
import com.jwt.implementation.model.Event;
import com.jwt.implementation.model.User;
import com.jwt.implementation.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        Event createdEvent = eventService.createEvent(eventDTO, username);

        EventDTO responseDTO = new EventDTO(
                createdEvent.getId(),
                createdEvent.getTitle(),
                createdEvent.getDescription(),
                createdEvent.getDate(),
                createdEvent.getLocation(),
                createdEvent.getFee(), // Include fee
                createdEvent.getParticipants().stream().map(User::getUserName).collect(Collectors.toList()),
                createdEvent.getHosts().stream().map(User::getUserName).collect(Collectors.toSet())
        );

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/allEvents")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();

        List<EventDTO> eventDTOs = events.stream().map(event -> new EventDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getLocation(),
                        event.getFee(), // Include fee
                        event.getParticipants().stream().map(User::getUserName).collect(Collectors.toList()),
                        event.getHosts().stream().map(User::getUserName).collect(Collectors.toSet())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String eventId) {
        Event event = eventService.getEventById(eventId);

        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        EventDTO eventDTO = new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getFee(), // Include fee
                event.getParticipants().stream().map(User::getUserName).collect(Collectors.toList()),
                event.getHosts().stream().map(User::getUserName).collect(Collectors.toSet()));

        return ResponseEntity.ok(eventDTO);
    }

    @GetMapping("/hostedByMeEvents")
    public ResponseEntity<List<EventDTO>> getHostedEvents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Event> events = eventService.gethostedByMeEvents(username);
        List<EventDTO> eventDTOs = events.stream().map(event -> new EventDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getLocation(),
                        event.getFee(), // Include fee
                        event.getParticipants().stream().map(User::getUserName).collect(Collectors.toList()),
                        event.getHosts().stream().map(User::getUserName).collect(Collectors.toSet())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/hostedByOthersEvents")
    public ResponseEntity<List<EventDTO>> getOtherEvents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Event> events = eventService.hostedByOthersEvents(username);

        List<EventDTO> eventDTOs = events.stream().map(event -> new EventDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getLocation(),
                        event.getFee(), // Include fee
                        event.getParticipants().stream().map(User::getUserName).collect(Collectors.toList()),
                        event.getHosts().stream().map(User::getUserName).collect(Collectors.toSet())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable String eventId,
            @RequestBody Map<String, Object> updates,
            Principal principal) {

        Event updatedEvent = eventService.updateEvent(eventId, updates, principal.getName());
        return ResponseEntity.ok(updatedEvent);
    }

    @PutMapping("/addhosts/{eventId}")
    public ResponseEntity<String> addEventHosts(
            @PathVariable String eventId,
            @RequestBody Map<String, String> requestBody,
            Principal principal) {
        try {
            String newHostUsername = requestBody.get("newHostUsername");
            String updatedEvent = eventService.addHosts(eventId, principal.getName(), newHostUsername);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("deletehost/{eventId}")
    public ResponseEntity<String> removeEventHost(
            @PathVariable String eventId,
            @RequestParam String hostName,
            Principal principal) {
        try {
            String responseMessage = eventService.removeHost(eventId, principal.getName(), hostName);
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove host.");
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PutMapping("/addParticipant/{eventId}")
    public ResponseEntity<String> addParticipant(@PathVariable String eventId, @RequestBody Map<String, String> requestBody) {
        try {
            String username = requestBody.get("username");
            String response = eventService.addParticipant(eventId, username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PutMapping("/removeParticipant/{eventId}")
    public ResponseEntity<String> removeParticipant(@PathVariable String eventId, @RequestParam String username) {
        try {
            String response = eventService.removeParticipant(eventId, username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Object> deleteEvent(@PathVariable String eventId, Principal principal) {
        try {
            eventService.deleteEvent(eventId, principal.getName());
            System.out.println("Event successfully deleted.");
            return generateResponse("Event deleted successfully", HttpStatus.OK, null);
        } catch (RuntimeException e) {
            System.out.println("Error deleting event: " + e.getMessage());
            return generateResponse("Error deleting event", HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    public ResponseEntity<Object> generateResponse(String message, HttpStatus st, Object responseobj) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("Status", st.value());
        map.put("data", responseobj);

        return new ResponseEntity<>(map, st);
    }
}
