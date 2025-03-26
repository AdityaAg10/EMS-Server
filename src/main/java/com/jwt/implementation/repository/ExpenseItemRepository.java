package com.jwt.implementation.repository;

import com.jwt.implementation.model.Expense;
import com.jwt.implementation.model.ExpenseItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseItemRepository extends MongoRepository<ExpenseItem, String> {
    List<ExpenseItem> findByExpenseId(Expense expense);
}

