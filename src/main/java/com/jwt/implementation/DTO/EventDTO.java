package com.jwt.implementation.DTO;

import java.util.List;
import java.util.Set;

public class EventDTO {
    private String id;
    private String title;
    private String description;
    private String date;
    private String location;
    private List<String> participants; // Store participant usernames
    private Set<String> hosts; // Store host usernames

    public EventDTO() {
    }


    public EventDTO(String title, String description, String date, String location, List<String> participants) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.participants = participants;
    }

    public String getId() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
    }

    public EventDTO(String id, String title, String description, String date, String location, List<String> participants, Set<String> hosts) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.hosts = hosts;
        this.participants = participants;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Set<String> getHosts() {
        return hosts;
    }

    public void setHosts(Set<String> hosts) {
        this.hosts = hosts;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", participants=" + participants +
                ", hosts=" + hosts +
                '}';
    }
}
