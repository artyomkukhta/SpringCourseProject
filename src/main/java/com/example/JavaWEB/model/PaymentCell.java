package com.example.JavaWEB.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public PaymentCell(String name, String type, String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public PaymentCell() { //важно для корректной работы
    }
}
