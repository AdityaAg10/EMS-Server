package com.jwt.implementation.service;

import com.jwt.implementation.model.Expense;
import com.jwt.implementation.model.ExpenseItem;
import com.jwt.implementation.model.User;
import com.jwt.implementation.model.Event;
import com.jwt.implementation.repository.ExpenseItemRepository;
import com.jwt.implementation.repository.ExpenseRepository;
import com.jwt.implementation.repository.UserRepository;
import com.jwt.implementation.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExpenseItemService {

    @Autowired
    private ExpenseItemRepository expenseItemRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    // Method to add a new expense item with access control
    public ExpenseItem addExpenseItem(String expenseId, String description, double amount) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!isAdminOrHost(expense.getEventId())) {
            throw new RuntimeException("Access Denied: Only admins or event hosts can add expense items.");
        }

        User createdBy = getAuthenticatedUser();
        ExpenseItem newItem = new ExpenseItem(description, amount, expense, createdBy);
        newItem.setLastUpdated(LocalDateTime.now()); // Set timestamp

        ExpenseItem savedItem = expenseItemRepository.save(newItem);

        expense.getItems().add(savedItem);
        expenseRepository.save(expense);

        return savedItem;
    }

    // Method to update an existing expense item with access control
    public ExpenseItem updateExpenseItem(String itemId, String description, double amount) {
        ExpenseItem existingItem = expenseItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Expense item not found"));

        if (!isAdminOrHost(existingItem.getExpense().getEventId())) {
            throw new RuntimeException("Access Denied: Only admins or event hosts can update expense items.");
        }

        existingItem.setDescription(description);
        existingItem.setAmount(amount);
        existingItem.setLastUpdated(LocalDateTime.now()); // Update timestamp

        return expenseItemRepository.save(existingItem);
    }

    // Method to delete an expense item (optional, but good for cleanup)
    public void deleteExpenseItem(String itemId) {
        ExpenseItem existingItem = expenseItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Expense item not found"));

        if (!isAdminOrHost(existingItem.getExpense().getEventId())) {
            throw new RuntimeException("Access Denied: Only admins or event hosts can delete expense items.");
        }

        expenseItemRepository.delete(existingItem);
    }

    // Check if the authenticated user is an admin or a host of the event
    private boolean isAdminOrHost(String eventId) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new RuntimeException("Event not found");
        }

        boolean isAdmin = user.getRole().contains("ROLE_ADMIN");
        boolean isHost = event.getHosts().contains(user);

        return isAdmin || isHost;
    }

    // Fetch the authenticated user
    private User getAuthenticatedUser() {
        String username = getCurrentUsername();
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    // Get the current authenticated user's username
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
