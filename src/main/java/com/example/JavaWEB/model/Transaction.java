package com.example.JavaWEB.model;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
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
    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}