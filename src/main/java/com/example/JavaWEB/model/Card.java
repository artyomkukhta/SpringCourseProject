package com.example.JavaWEB.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity

@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;
    @NotNull
    @Size(min=3, message = "Card name must be at least 3 characters long")
    private String name;
    @NotNull
    //@CreditCardNumber(ignoreNonDigitCharacters = true ,message = "Not a valid credit card number")
    @Size(min = 19, max = 19, message = "Not a valid credit card number")
    private String number;

    private double balance;
    @NotNull
    private boolean isBlocked;

    private double operationSum;

    @ManyToOne
    @JoinColumn(name = "card_type_id")
    private CardType cardType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "card", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Card(String name, CardType cardType, String number, double balance, boolean isBlocked) {
        this.cardType = cardType;
        this.name = name;
        this.number = number;
        this.balance = balance;
        this.isBlocked = isBlocked;
        this.operationSum = 0;
    }


    public Card() {
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public double getOperationSum() {
        return operationSum;
    }

    public void setOperationSum(double operationSum) {
        this.operationSum = operationSum;
    }
}
