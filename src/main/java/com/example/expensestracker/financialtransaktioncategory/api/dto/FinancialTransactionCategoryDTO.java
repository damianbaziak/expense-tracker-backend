package com.example.expensestracker.financialtransaktioncategory.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class FinancialTransactionCategoryDTO {
    private Long id;
    private String name;
    private FinancialTransactionType type;
    private Instant creationDate;
    private Long userId;
}


