package com.example.JavaWEB.repository;

import com.example.JavaWEB.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardTypeRepository extends JpaRepository<CardType, Long> {

    Optional<CardType> findByName(String name);

}
