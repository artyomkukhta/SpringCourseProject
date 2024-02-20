package com.example.JavaWEB.repository;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;


@EnableJpaRepositories
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findAllByUser(User user, Pageable pageable);
}
