package com.example.expensestracker.financialtransaction.api;

import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionUpdateDTO;

import java.util.List;

public interface FinancialTransactionService {
    FinancialTransactionDTO createFinancialTransaction(
            FinancialTransactionCreateDTO financialTransactionCreateDTO, Long userId);

    FinancialTransactionDTO updateFinancialTransaction(Long financialTransactionId,
            FinancialTransactionUpdateDTO financialTransactionUpdateDTO, Long userId);

    List<FinancialTransactionDTO> findFinancialTransactionsByWalletId(Long walletId, Long userId);

    FinancialTransactionDTO findFinancialTransactionForUser(Long id, Long userId);

    void deleteTransaction(Long id, Long userId);
}
