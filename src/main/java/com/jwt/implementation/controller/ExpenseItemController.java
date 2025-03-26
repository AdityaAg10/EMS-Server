package com.jwt.implementation.controller;

import com.jwt.implementation.model.ExpenseItem;
import com.jwt.implementation.service.ExpenseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense-items")
public class ExpenseItemController {

    @Autowired
    private ExpenseItemService expenseItemService;

    @PostMapping("/add/{expenseId}")
    public ResponseEntity<ExpenseItem> addExpenseItem(
            @PathVariable String expenseId,
            @RequestBody ExpenseItem expenseItem) {

        ExpenseItem newItem = expenseItemService.addExpenseItem(expenseId, expenseItem.getDescription(), expenseItem.getAmount());
        return ResponseEntity.ok(newItem);
    }


    @PutMapping("/update/{itemId}")
    public ResponseEntity<ExpenseItem> updateExpenseItem(
            @PathVariable String itemId,
            @RequestParam String description,
            @RequestParam double amount) {

        System.out.println(description + amount);
        ExpenseItem updatedItem = expenseItemService.updateExpenseItem(itemId, description, amount);
        return ResponseEntity.ok(updatedItem);
    }
}

