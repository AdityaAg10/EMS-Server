package com.jwt.implementation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "expenses")
public class Expense {

    @Id
    private String id;

    private String category;  // e.g., "Catering", "Venue", "Marketing"

    private String eventId;  // 🔗 Reference to the associated Event by its ID

    @DBRef
    private Set<ExpenseItem> items = new HashSet<>();  // 💰 Detailed breakdown

    public Expense() {
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", eventId='" + eventId + '\'' +
                ", items=" + items +
                '}';
    }

    public Expense(String category, String eventId, Set<ExpenseItem> items) {
        this.category = category;
        this.eventId = eventId;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Set<ExpenseItem> getItems() {
        return items;
    }

    public void setItems(Set<ExpenseItem> items) {
        this.items = items;
    }

    public double getTotalExpense() {
        return items.stream().mapToDouble(ExpenseItem::getAmount).sum();
    }
}
