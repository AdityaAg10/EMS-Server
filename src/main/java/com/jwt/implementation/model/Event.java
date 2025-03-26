    package com.jwt.implementation.model;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.DBRef;

    import java.util.HashSet;
    import java.util.Set;

    @Document(collection = "events")  // MongoDB equivalent of @Entity
    public class Event {

        @Id
        private String id;  // MongoDB uses String IDs
        private String title;
        private String description;
        private String date;
        private String location;
        private double fee;

        // Storing references to User IDs instead of using @ManyToMany
        @DBRef
        private Set<User> hosts = new HashSet<>();

        @DBRef
        private Set<User> participants = new HashSet<>();

        public Event() {
        }

        public Event(String title, String description, String date, String location, double fee, Set<User> hosts, Set<User> participants) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.location = location;
            this.fee = fee;
            this.hosts = hosts;
            this.participants = participants;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", date='" + date + '\'' +
                    ", location='" + location + '\'' +
                    ", fee=" + fee +
                    ", hosts=" + hosts +
                    ", participants=" + participants +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public Set<User> getHosts() {
            return hosts;
        }

        public void setHosts(Set<User> hosts) {
            this.hosts = hosts;
        }

        public Set<User> getParticipants() {
            return participants;
        }

        public void setParticipants(Set<User> participants) {
            this.participants = participants;
        }

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }

    }
