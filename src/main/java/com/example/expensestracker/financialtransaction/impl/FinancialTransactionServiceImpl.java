package com.example.expensestracker.financialtransaction.impl;

import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionService;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionUpdateDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinancialTransactionServiceImpl implements FinancialTransactionService {
    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private FinancialTransactionCategoryRepository financialTransactionCategoryRepository;

    @Autowired
    private FinancialTransactionModelMapper financialTransactionModelMapper;

    @Override
    public FinancialTransactionDTO createFinancialTransaction(
            FinancialTransactionCreateDTO ftCreateDTO, Long userId) {

        Long walletId = ftCreateDTO.getWalletId();
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId).orElseThrow(() ->
                new AppRuntimeException(ErrorCode.W001, String.format("Wallet with this id: %d not exist", walletId)));

        FinancialTransactionCategory ftCategory = findFinancialTransactionCategory(ftCreateDTO.getCategoryId(), userId);

        if (ftCreateDTO.getCategoryId() != null && ftCreateDTO.getType() != ftCategory.getType())
            throw new AppRuntimeException(ErrorCode.FT002, String.format(
                    "Financial transaction type: %s does not match financial category type: %s",
                    ftCreateDTO.getType(), ftCategory.getType()));

        FinancialTransaction financialTransaction = buildFinancialTransaction(ftCreateDTO, wallet, ftCategory);
        FinancialTransaction savedFinancialTransaction = financialTransactionRepository.save(financialTransaction);

        return financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                savedFinancialTransaction);

    }

    @Override
    public List<FinancialTransactionDTO> findFinancialTransactionsByWalletId(Long walletId, Long userId) {

        walletRepository.findByIdAndUserId(walletId, userId).orElseThrow(() ->
                new AppRuntimeException(ErrorCode.W001, String.format("Wallet with this id: %d not exist", walletId)));

        return financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(walletId, userId).stream()
                .map(financialTransaction -> financialTransactionModelMapper
                        .mapFinancialTransactionEntityToFinancialTransactionDTO(financialTransaction))
                .toList();


    }

    @Override
    @Transactional
    public FinancialTransactionDTO updateFinancialTransaction(
            Long financialTransactionId, FinancialTransactionUpdateDTO financialTransactionUpdateDTO, Long userId) {

        FinancialTransaction financialTransaction = financialTransactionRepository.findByIdAndWalletUserId(
                financialTransactionId, userId).orElseThrow(() -> new AppRuntimeException(ErrorCode.FT001, String.format(
                "Financial transaction with this id: %d not exist", financialTransactionId)));

        if (financialTransactionUpdateDTO.getCategoryId() != null) {
            FinancialTransactionCategory financialTransactionCategory = findFinancialTransactionCategory(
                    financialTransactionUpdateDTO.getCategoryId(), userId);

            if (financialTransactionUpdateDTO.getType() != financialTransactionCategory.getType()) {
                throw new AppRuntimeException(ErrorCode.FT002, String.format(
                        "Financial transaction type: %s does not match financial category type: %s",
                        financialTransactionUpdateDTO.getType(), financialTransactionCategory.getType()));
            }

            financialTransaction.setFinancialTransactionCategory(financialTransactionCategory);
        }
        financialTransaction.setAmount(financialTransactionUpdateDTO.getAmount());
        financialTransaction.setDescription(financialTransactionUpdateDTO.getDescription());
        financialTransaction.setDate(financialTransactionUpdateDTO.getDate());
        financialTransaction.setType(financialTransactionUpdateDTO.getType());

        return financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                financialTransaction);
    }

    @Override
    public FinancialTransactionDTO findFinancialTransactionForUser(Long id, Long userId) {
        Optional<FinancialTransaction> financialTransaction = financialTransactionRepository.findByIdAndWalletUserId(
                id, userId);
        if (financialTransaction.isEmpty()) {
            throw new AppRuntimeException(ErrorCode.FT001, String.format(
                    "Financial transaction with id: %d does not found", id));
        }
        FinancialTransaction existedTransaction = financialTransaction.get();
        return financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                existedTransaction);
    }

    @Override
    public void deleteTransaction(Long id, Long userId) {
        if (financialTransactionRepository.existsByIdAndWalletUserId(id, userId)) {
            financialTransactionRepository.deleteById(id);
        } else
            throw new AppRuntimeException(ErrorCode.FT001, String.format(
                    "Financial transaction with this id: %d does not exist", id));
    }

    private FinancialTransactionCategory findFinancialTransactionCategory(Long categoryId, Long userId) {
        if (categoryId != null) {
            Optional<FinancialTransactionCategory> ftCategory = financialTransactionCategoryRepository
                    .findByIdAndUserId(categoryId, userId);
            if (ftCategory.isEmpty()) {
                throw new AppRuntimeException(ErrorCode.FTC001, String.format(
                        "Financial transaction category with this id: %d not exist", categoryId));
            }
            return ftCategory.get();
        }
        return null;

    }

    private FinancialTransaction buildFinancialTransaction(FinancialTransactionCreateDTO ftCreateDTO, Wallet wallet,
                                                           FinancialTransactionCategory ftCategory) {
        return FinancialTransaction.builder()
                .wallet(wallet)
                .type(ftCreateDTO.getType())
                .amount(ftCreateDTO.getAmount())
                .date(ftCreateDTO.getDate())
                .financialTransactionCategory(ftCategory)
                .description(ftCreateDTO.getDescription())
                .build();
    }
}


