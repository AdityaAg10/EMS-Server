package com.jwt.implementation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "expense_items")
public class ExpenseItem {

    @Id
    private String id;

    private String description;  // 📝 What was bought? (e.g., "DJ Setup", "Buffet Dinner")
    private double amount;

    @JsonIgnore
    @DBRef
    private Expense expense;  // 🔗 Reference to the associated Expense (by its ID)

    @DBRef
    private User addedBy; // 🔍 Who added/updated this item

    private LocalDateTime lastUpdated; // ⏳ Last update timestamp

    public ExpenseItem() {
    }

    public ExpenseItem(String description, double amount, Expense expense, User addedBy) {
        this.description = description;
        this.amount = amount;
        this.expense = expense;
        this.addedBy = addedBy;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ExpenseItem{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", expense=" + expense +
                ", addedBy=" + addedBy +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
