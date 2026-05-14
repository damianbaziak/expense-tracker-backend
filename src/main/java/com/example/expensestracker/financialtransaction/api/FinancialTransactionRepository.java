package com.example.expensestracker.financialtransaction.api;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface FinancialTransactionRepository extends CrudRepository<FinancialTransaction, Long> {
    Optional<FinancialTransaction> findByIdAndWalletUserId(Long financialTransactionId, Long walletId);

    List<FinancialTransaction> findAllByWalletIdAndWalletUserIdOrderByDateDesc(Long walletId, Long userId);

    boolean existsByIdAndWalletUserId(Long id, Long userId);

    BigInteger countFinancialTransactionsByFinancialTransactionCategoryId(Long categoryId);
}
