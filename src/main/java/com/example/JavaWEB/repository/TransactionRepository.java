package com.example.JavaWEB.repository;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByCardIn(List<Card> cards, Pageable pageable);
}
