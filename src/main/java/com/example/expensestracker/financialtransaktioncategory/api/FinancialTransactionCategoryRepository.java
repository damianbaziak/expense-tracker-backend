package com.example.expensestracker.financialtransaktioncategory.api;

import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialTransactionCategoryRepository extends CrudRepository<FinancialTransactionCategory, Long> {
    List<FinancialTransactionCategory> findAllByUserId(Long userId);

    Optional<FinancialTransactionCategory> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
