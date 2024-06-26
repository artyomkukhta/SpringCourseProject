package com.example.JavaWEB.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;
    private String name;
    @Min(value = 0, message = "Amount must be above 0")
    private double amount;
    private String date;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;



    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    public Transaction() {
    }

    public Transaction(String name, Card card, double amount, String date, TransactionStatus status) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.card = card;
    }

}