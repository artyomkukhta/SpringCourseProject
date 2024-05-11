package com.example.JavaWEB.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
//@Table(name="paymentCells")
public class PaymentCell {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;
    @NotNull
    @Size(min = 3, message = "Service name must be at least 3 characters long")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;
    @NotNull
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String desc;


    public PaymentCell(String name, String type, String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public PaymentCell() {
    }
}
