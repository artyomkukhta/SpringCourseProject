package com.example.JavaWEB.service;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.CardType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CardTypeService{
    public Iterable<CardType> findAll();

    public Optional<CardType> findById(Long id);
    public Optional<CardType> findByName(String name);

    public boolean add(Long userId, CardType cardType);


    public boolean delete(CardType cardType);

    public boolean deleteById(Long id);

    public boolean editById(Long id, CardType cardType);
    public boolean existsById(Long id);
    public CardType save(CardType cardType);
}
