package com.example.expensestracker.financialtransaktioncategory.api;

import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryCreateDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDetailedDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryUpdateDTO;

import java.util.List;

public interface FinancialTransactionCategoryService {
    FinancialTransactionCategoryDTO createCategory(
            FinancialTransactionCategoryCreateDTO categoryCreateDTO, Long userID);

    FinancialTransactionCategoryDetailedDTO findFinancialTransactionCategoryForUser(Long id, Long userID);

    List<FinancialTransactionCategoryDTO> findFinancialTransactionCategories(Long userId);

    void deleteCategory(Long id, Long userId);

    FinancialTransactionCategoryDTO updateFinancialTransactionCategory(
            Long id, FinancialTransactionCategoryUpdateDTO categoryUpdateDTO, Long userId);
}
