package com.example.expensestracker.financialtransaktioncategory.api;

import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialTransactionCategoryRepository extends JpaRepository<FinancialTransactionCategory, Long> {
    List<FinancialTransactionCategory> findAllByUserId(Long userId);

    Optional<FinancialTransactionCategory> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
