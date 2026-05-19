package com.example.expensestracker.financialtransaction.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionCreateDTO {

    @Min(1)
    @NotNull
    private Long walletId;

    //The @Digits(integer = 12, fraction = 2) annotation in Java ensures that a number:
    //Has a maximum of 12 digits before the decimal point.
    //Has a maximum of 2 digits after the decimal point.
    //It validates the format of the number, enforcing specific precision.
    @Digits(integer = 12, fraction = 2)
    @PositiveOrZero
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    @NotNull
    private FinancialTransactionType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant date;

    private Long categoryId;
}

