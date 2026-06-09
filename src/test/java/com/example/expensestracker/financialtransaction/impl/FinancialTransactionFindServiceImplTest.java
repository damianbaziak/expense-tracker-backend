package com.example.expensestracker.financialtransaction.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FinancialTransactionFindServiceImplTest {
    private static final Long ID_1L = 1L;
    private static final Long WALLET_ID_1L = 1L;
    private static final Long USER_ID_1L = 1L;

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private FinancialTransactionModelMapper financialTransactionModelMapper;

    @InjectMocks
    private FinancialTransactionServiceImpl financialTransactionService;

    @Test
    @DisplayName("Should return financial transactions DTOs")
    void findFinancialTransactionsByWalletId_transactionsExist_returnFinancialTransactionDTOs() {
        // given
        User user = TestUtils.createUser();
        Wallet wallet = TestUtils.createWallet(WALLET_ID_1L, user);

        List<FinancialTransaction> ftList = TestUtils.createTransactions(
                3, wallet, EXPENSE);

        when(walletRepository.findByIdAndUserId(WALLET_ID_1L, user.getId())).thenReturn(Optional.of(wallet));
        when(financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(
                WALLET_ID_1L, user.getId())).thenReturn(ftList);
        when(financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                any(FinancialTransaction.class)))
                .thenAnswer(invocation -> {
                    FinancialTransaction transaction = invocation.getArgument(0);
                    return new FinancialTransactionDTO(
                            transaction.getId(), transaction.getAmount(), transaction.getDescription(),
                            transaction.getType(), transaction.getDate(), ID_1L);
                });

        // when
        List<FinancialTransactionDTO> result = financialTransactionService.findFinancialTransactionsByWalletId(
                WALLET_ID_1L, ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals(1L, result.get(0).getId()),
                () -> assertEquals(2L, result.get(1).getId()),
                () -> assertEquals(ftList.get(0).getDescription(), result.get(0).getDescription()),
                () -> assertEquals(ftList.get(1).getDescription(), result.get(1).getDescription()),
                () -> assertEquals(new BigDecimal(101), result.get(0).getAmount()),
                () -> assertEquals(new BigDecimal(103), result.get(2).getAmount())
        );
        verify(walletRepository, atMostOnce()).findByIdAndUserId(any(), any());
    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when wallet not exist")
    void findFinancialTransactionsByWalletId_walletNotExist_throwAppRuntimeException() {
        // given
        User user = TestUtils.createUser();

        when(walletRepository.findByIdAndUserId(2L, user.getId())).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> financialTransactionService.findFinancialTransactionsByWalletId(2L, user.getId()));

        Assertions.assertEquals(ErrorCode.W001.getBusinessMessage(), result.getMessage());
        Assertions.assertEquals(ErrorCode.W001.getHttpStatus(), result.getHttpStatusCode());
        verify(financialTransactionRepository, never()).findAllByWalletIdAndWalletUserIdOrderByDateDesc(any(), any());
    }

    @Test
    @DisplayName("Should return an empty List when there are no financial transactions in the wallet")
    void findFinancialTransactionsByWalletId_noFinancialTransactions_returnEmptyList() {
        // given
        User user = TestUtils.createUser();
        Wallet wallet = TestUtils.createWallet(WALLET_ID_1L, user);

        when(walletRepository.findByIdAndUserId(WALLET_ID_1L, user.getId())).thenReturn(Optional.of(wallet));
        when(financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(
                WALLET_ID_1L, user.getId())).thenReturn(Collections.emptyList());

        // when
        List<FinancialTransactionDTO> result = financialTransactionService.findFinancialTransactionsByWalletId(
                WALLET_ID_1L, ID_1L);

        // then
        Assertions.assertTrue(result.isEmpty());
        verify(walletRepository, atMostOnce()).findByIdAndUserId(any(), any());
    }

    @Test
    @DisplayName("Should retrieve transaction by ID and walletUserId")
    void findFinancialTransactionForUser_whenTransactionExist_shouldReturnTransaction() {
        // given
        FinancialTransactionDTO financialTransactionDTO = TestUtils.createFinancialTransactionDTO(INCOME);
        FinancialTransactionCategory financialTransactionCategory = TestUtils.createTransactionCategory(
                ID_1L, INCOME);
        FinancialTransaction financialTransaction = TestUtils.createTransactionForTest(
                INCOME, financialTransactionCategory);

        // Mocking repository and service
        when(financialTransactionRepository.findByIdAndWalletUserId(ID_1L, USER_ID_1L)).thenReturn(
                Optional.of(financialTransaction));
        when(financialTransactionService.findFinancialTransactionForUser(ID_1L, USER_ID_1L)).thenReturn(
                financialTransactionDTO);

        // when
        FinancialTransactionDTO result = financialTransactionService.findFinancialTransactionForUser(ID_1L, USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(result.getId(), financialTransactionDTO.getId()),
                () -> assertEquals(result.getAmount(), financialTransactionDTO.getAmount()),
                () -> assertEquals(INCOME, result.getType()),
                () -> assertEquals(result.getDate(), financialTransactionDTO.getDate()));

    }

    @Test
    @DisplayName("Should throw AppRuntimeException when transaction not exist")
    void findFinancialTransactionForUser_whenTransactionNotExist_shouldThrowAppRuntimeException() {
        // given
        when(financialTransactionRepository.findByIdAndWalletUserId(ID_1L, USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> financialTransactionService.findFinancialTransactionForUser(1L, USER_ID_1L));

        Assertions.assertEquals(ErrorCode.FT001.getBusinessMessage(), result.getMessage());
        Assertions.assertEquals(ErrorCode.FT001.getHttpStatus(), result.getHttpStatusCode());
        verify(financialTransactionModelMapper, never()).mapFinancialTransactionEntityToFinancialTransactionDTO(any());

    }


}