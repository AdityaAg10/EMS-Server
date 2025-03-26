package com.jwt.implementation.service;

import com.jwt.implementation.DTO.EventDTO;
import com.jwt.implementation.model.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventService {
    Event createEvent(EventDTO eventDTO, String username);
    void deleteEvent(String eventId, String username);
    Event updateEvent(String eventId, Map<String, Object> updates, String username);

    List<Event> getAllEvents();

    List<Event> gethostedByMeEvents(String username);
    List<Event> hostedByOthersEvents(String username);

    Event getEventById(String eventId);

    String addHosts(String eventId, String requestorUsername, String newHostUsername);
    String removeHost(String eventId, String name, String hostName);

    String addParticipant(String eventId, String username);
    String removeParticipant(String eventId, String username);
}
