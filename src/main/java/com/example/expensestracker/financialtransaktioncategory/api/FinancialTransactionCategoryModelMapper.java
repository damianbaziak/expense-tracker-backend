package com.example.expensestracker.financialtransaktioncategory.api;

import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialTransactionCategoryModelMapper {

    @Mapping(source = "user.id", target = "userId")
    FinancialTransactionCategoryDTO mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(
            FinancialTransactionCategory financialTransactionCategory);
}
