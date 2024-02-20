package com.example.JavaWEB.service;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.CardType;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CardService {
    public Iterable<Card> findAll();

    public Optional<Card> findById(Long id);

    public boolean add(Long userId, Card card);

    public boolean add(Long userId, String name, CardType cardType, String number, double balance, boolean isBlocked);

    public boolean delete(Card card);

    public boolean deleteById(Long id);

    public boolean editById(Long id, String name, String number, double balance, boolean isBlocked);
    public boolean existsById(Long id);

    public Page<Card> findPaginated(Long userId, int pageNo, int pageSize, String sortField, String sortDirection);

    public boolean addBalanceById(Long cardId, double amount);

}
