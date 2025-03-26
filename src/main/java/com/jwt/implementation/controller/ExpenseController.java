package com.jwt.implementation.controller;

import com.jwt.implementation.model.Expense;
import com.jwt.implementation.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/add/{eventId}")
    public ResponseEntity<Expense> addExpense(@PathVariable String eventId, @RequestBody Expense expense) {
        Expense savedExpense = expenseService.addExpense(eventId, expense.getCategory(), expense.getItems());
        return ResponseEntity.ok(savedExpense);
    }

    @GetMapping("/total/{eventId}")
    public ResponseEntity<Double> getTotalSpent(@PathVariable String eventId) {
        return ResponseEntity.ok(expenseService.getTotalSpent(eventId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<Expense>> getExpensesByEvent(@PathVariable String eventId) {
//        System.out.println(expenseService.getExpensesByEvent(eventId));
        return ResponseEntity.ok(expenseService.getExpensesByEvent(eventId));
    }
}

