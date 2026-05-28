package com.example.expensestracker.wallet.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.dto.WalletUpdateDTO;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletUpdateServiceImplTest {

    private static final Long USER_ID_1L = 1L;
    private static final Long WALLET_ID_1L = 1L;
    private static final Long TRANSACTION_ID_1L = 1L;
    private static final String NEW_WALLET_NAME = "New wallet name";
    private static final BigDecimal INCOME_AMOUNT = new BigDecimal(100);
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal(50);

    @InjectMocks
    private WalletServiceImpl walletService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private FinancialTransactionRepository financialTransactionRepository;
    @Mock
    private FinancialTransactionModelMapper financialTransactionModelMapper;

    @Test
    @DisplayName("Should update wallet and return walletDTO")
    void updateWallet_walletExists_shouldReturnWalletDTO() {
        // given
        User user = TestUtils.createUser();
        Wallet wallet = TestUtils.createWallet(WALLET_ID_1L, user);
        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_WALLET_NAME);

        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));

        // when
        WalletDTO result = walletService.updateWallet(WALLET_ID_1L, updateDTO, USER_ID_1L);

        // then
        Assertions.assertEquals(updateDTO.getName(), result.getName());

    }

    @Test
    @DisplayName("Should update wallet and calculate balance based on given transactions")
    void updateWallet_walletExists_calculatesCorrectBalance() {
        // given
        User user = TestUtils.createUser();
        Wallet wallet = TestUtils.createWallet(WALLET_ID_1L, user);
        List<FinancialTransaction> transactions = createTestTransactions();

        mockTransactionRepositoryAndMapperBehaviour(wallet, transactions);

        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_WALLET_NAME);

        // when
        WalletDTO result = walletService.updateWallet(WALLET_ID_1L, updateDTO, USER_ID_1L);

        //then
        Assertions.assertEquals(updateDTO.getName(), result.getName());
        Assertions.assertEquals(INCOME_AMOUNT.subtract(EXPENSE_AMOUNT), result.getBalance());

    }

    @Test
    @DisplayName("Should throw an AppRuntimeException")
    void updateWallet_walletNotExists_throwAnException() {
        // given
        User user = TestUtils.createUser();
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.empty());
        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_WALLET_NAME);
        // when
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> walletService.updateWallet(WALLET_ID_1L, updateDTO, user.getId()));

        // then
        Assertions.assertAll(
                () -> assertEquals(ErrorCode.W001.getBusinessMessage(), result.getMessage()),
                () -> assertEquals(ErrorCode.W001.getHttpStatus(), result.getHttpStatusCode()),
                () -> assertEquals(ErrorCode.W001.getBusinessCode(), result.getBusinessCode()));
        verify(walletRepository, times(1)).findById(WALLET_ID_1L);
        verify(walletRepository, times(0)).deleteById(WALLET_ID_1L);


    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when user has no permissions to update that wallet")
    void updateWallet_userHasNoPermissions_throwAnException() {
        // given
        User user = TestUtils.createUser();
        Wallet wallet = TestUtils.createWallet(WALLET_ID_1L, user);
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));
        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_WALLET_NAME);

        // when
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> walletService.updateWallet(WALLET_ID_1L, updateDTO, 7L));

        // then
        Assertions.assertAll(
                () -> assertEquals(ErrorCode.W002.getBusinessMessage(), result.getMessage()),
                () -> assertEquals(ErrorCode.W002.getHttpStatus(), result.getHttpStatusCode()),
                () -> assertEquals(ErrorCode.W002.getBusinessCode(), result.getBusinessCode()));
        verify(walletRepository, times(1)).findById(WALLET_ID_1L);
        verify(walletRepository, times(0)).deleteById(WALLET_ID_1L);


    }

    private void mockTransactionRepositoryAndMapperBehaviour(Wallet wallet, List<FinancialTransaction> transactions) {

        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));
        when(financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(WALLET_ID_1L, USER_ID_1L))
                .thenReturn(transactions);

        for (FinancialTransaction transaction : transactions) {

            FinancialTransactionDTO transactionDTO = new FinancialTransactionDTO(
                    transaction.getId(),
                    transaction.getAmount(),
                    transaction.getDescription(),
                    transaction.getType(),
                    transaction.getDate(),
                    transaction.getFinancialTransactionCategory() != null
                            ? transaction.getFinancialTransactionCategory().getId() : null
            );

            when(financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(transaction))
                    .thenReturn(transactionDTO);
        }

    }

    List<FinancialTransaction> createTestTransactions() {
        FinancialTransaction transaction1 = TestUtils.createTransactionForTest(TRANSACTION_ID_1L, INCOME);
        transaction1.setAmount(INCOME_AMOUNT);
        FinancialTransaction transaction2 = TestUtils.createTransactionForTest(TRANSACTION_ID_1L, EXPENSE);
        transaction2.setAmount(EXPENSE_AMOUNT);

        return Arrays.asList(transaction1, transaction2);
    }
}