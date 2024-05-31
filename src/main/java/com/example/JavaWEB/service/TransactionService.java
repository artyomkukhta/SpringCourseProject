package com.example.JavaWEB.service;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.Transaction;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.Set;

public interface TransactionService {
    public Set<Transaction> findTransactionsByUserId(Long userId);

    public Optional<Transaction> findById(Long id);

    public boolean add(Transaction transaction);

    public boolean add(String name, Card selectedCard, double amount);

    public boolean delete(Transaction transaction);

    public boolean deleteById(Long id);

    public String handleTransaction(Long cellId, Card selectedCard, double amount) throws Exception;

    public Page<Transaction> findPaginated(Long userId, int pageNo, int pageSize, String sortField, String sortDirection);
     void transferByNumber(Long fromCardId, String toCardNumber, Double amount);
     void transferById(Long fromCardId, Long toCardId, Double amount);
}
