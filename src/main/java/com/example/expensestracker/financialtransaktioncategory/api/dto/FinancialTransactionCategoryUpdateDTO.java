package com.example.expensestracker.financialtransaktioncategory.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FinancialTransactionCategoryUpdateDTO(
        @NotBlank
        @Size(max = 30, message = "Name size too long")
        @Pattern(regexp = "^[\\w\\s]+$")
        String name,
        @NotNull
        FinancialTransactionType type) {
}
