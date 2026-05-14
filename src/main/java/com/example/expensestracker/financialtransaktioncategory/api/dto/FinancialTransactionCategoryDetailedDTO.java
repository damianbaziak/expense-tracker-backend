package com.example.expensestracker.financialtransaktioncategory.api.dto;

import java.math.BigInteger;

public record FinancialTransactionCategoryDetailedDTO(
        FinancialTransactionCategoryDTO financialTransactionCategoryDTO, BigInteger financialTransactionCounter) {
}
