package com.example.JavaWEB.service;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.User;

import java.util.Optional;

public interface UserService {
    public Iterable<User> findAll();

    public Optional<User> findById(Long id);

    public User findByUsername(String username);

    public Iterable<Card> findCardsByUserId(Long userId);

    public Iterable<Card> findAllUserCards();

    public boolean add(User user);

    public boolean delete(User user);

    public boolean deleteById(Long id);

    public boolean edit(User user, String username, String email, String password);

    public boolean editById(Long id, String username, String email, String password);

    public User findUserByAuth();
}
