package com.example.JavaWEB.model.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardView {
    private Long id;
    @NotNull
    @Size(min=3, message = "Card name must be at least 3 characters long")
    private String name;
    @NotNull

    @Size(min = 19, max = 19, message = "Not a valid credit card number")
    @Pattern(regexp = "^\\d{4} \\d{4} \\d{4} \\d{4}$", message = "Invalid credit card number format")
    private String number;

    private double balance;
    @NotNull
    private boolean isBlocked;

    private double operationSum;

    @NotNull
    @Size(min=1, message = "Card name must be at least 3 characters long")
    private String cardType;
}
