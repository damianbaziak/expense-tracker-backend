package com.example.expensestracker.financialtransaction.api;

import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialTransactionModelMapper {

    @Mapping(source = "financialTransactionCategory.id", target = "categoryId")
    FinancialTransactionDTO mapFinancialTransactionEntityToFinancialTransactionDTO(
            FinancialTransaction financialTransaction);
}
