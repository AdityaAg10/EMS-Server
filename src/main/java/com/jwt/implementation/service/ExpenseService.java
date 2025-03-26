package com.jwt.implementation.service;

import com.jwt.implementation.model.Event;
import com.jwt.implementation.model.Expense;
import com.jwt.implementation.model.ExpenseItem;
import com.jwt.implementation.model.User;
import com.jwt.implementation.repository.EventRepository;
import com.jwt.implementation.repository.ExpenseItemRepository;
import com.jwt.implementation.repository.ExpenseRepository;
import com.jwt.implementation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ExpenseItemRepository expenseItemRepo;

    @Autowired
    private EventRepository eventRepo;

    public Expense addExpense(String eventId, String category, Set<ExpenseItem> items) {

        User currentUser;
        try {
            String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            currentUser = userRepo.findByUserName(currentUserUsername);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve current user: " + e.getMessage());
        }
        // Fetch the event
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if the user is an admin or the host of the event
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        boolean isHost = event.getHosts().contains(currentUser); // Assuming event has a `hosts` list

        if (!isAdmin && !isHost) {
            throw new RuntimeException("You do not have permission to add an expense for this event.");
        }

        // Ensure items is not null
        if (items == null) {
            items = new HashSet<>();
        }

        // Create and save the expense
        Expense expense = new Expense(category, eventId, items);
        return expenseRepository.save(expense);
    }



    public double getTotalSpent(String eventId) {
        List<Expense> expenses = expenseRepository.findByEventId(eventId);
        return expenses.stream().mapToDouble(Expense::getTotalExpense).sum();
    }

    public List<Expense> getExpensesByEvent(String eventId) {
        return expenseRepository.findByEventId(eventId);
    }
}
