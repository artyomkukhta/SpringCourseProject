package com.example.JavaWEB.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@RequiredArgsConstructor
public class CardType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cardType")
    private Set<Card> cards;

    @NotNull
    @Min(value = 0, message = "Cashback cannot be negative")
    private Double cashback;

    @NotNull
    @Min(value = 0, message = "Commission cannot be negative")
    private Double commission;

    @NotNull
    @Min(value = 0, message = "Limit cannot be negative")
    private Double limit;
}
