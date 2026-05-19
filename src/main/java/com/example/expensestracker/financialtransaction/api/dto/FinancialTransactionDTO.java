package com.example.expensestracker.financialtransaction.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialTransactionDTO {

    private Long id;

    private BigDecimal amount;

    // The @JsonInclude(JsonInclude.Include.ALWAYS) annotation in Java ensures that the description field is always
    // included when the object is serialized to JSON, regardless of whether its value is null, empty, or the default.
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String description;

    private FinancialTransactionType type;

    private Instant date;

    private Long categoryId;
}