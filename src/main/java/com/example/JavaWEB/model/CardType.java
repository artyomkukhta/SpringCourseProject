package com.example.JavaWEB.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Getter
@Setter

public class CardType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;



    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cardType")
    private Set<Card> cards;

    private Double cashback;

    private Double commission;

    private Double limit;

    // Constructors, getters, and setters

    public CardType() {
    }

    public CardType(String name, String cardType, Double cashback, Double commission, Double limit) {
        this.name = name;
        this.cashback = cashback;
        this.commission = commission;
        this.limit = limit;
    }


    // Getters and setters

    // Other methods

    // You can add other methods as needed
}

