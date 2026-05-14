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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletFindServiceImplTest {
    private static final Long USER_ID_1L = 1L;
    private static final Long WALLET_ID_1L = 1L;
    private static final BigDecimal INCOME_AMOUNT_1 = new BigDecimal(100);
    private static final BigDecimal INCOME_AMOUNT_2 = new BigDecimal(200);
    private static final BigDecimal INCOME_AMOUNT_3 = new BigDecimal(300);
    private static final BigDecimal EXPENSE_AMOUNT_1 = new BigDecimal(50);
    private static final BigDecimal EXPENSE_AMOUNT_2 = new BigDecimal(100);
    private static final BigDecimal EXPENSE_AMOUNT_3 = new BigDecimal(150);

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private FinancialTransactionModelMapper financialTransactionModelMapper;
    @Mock
    private FinancialTransactionRepository financialTransactionRepository;
    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    @DisplayName("Should return walletDTO by existing wallet")
    void findById_whenWalletExists_shouldReturnsWalletDTO() {
        // given
        Wallet wallet = TestUtils.createWalletForTest(User.builder().id(USER_ID_1L).build());
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));

        // when
        WalletDTO result = walletService.findById(WALLET_ID_1L, USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(WALLET_ID_1L, result.getId()),
                () -> assertEquals(USER_ID_1L, result.getUserId()),
                () -> assertEquals(wallet.getName(), result.getName()),
                () -> assertEquals(wallet.getCreationDate(), result.getCreationDate())
        );
    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when wallet not found")
    void findById_walletNotFound_shouldThrowsException() {
        // given
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class,
                () -> walletService.findById(WALLET_ID_1L, USER_ID_1L));

        Assertions.assertAll(
                () -> assertEquals(ErrorCode.W001, result.getErrorCode()));
    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when user has no permissions")
    void findById_userHasNoPermissions_shouldThrowsException() {
        // given
        Wallet wallet = TestUtils.createWalletForTest(User.builder().id(USER_ID_1L).build());
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));

        // when and then
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class,
                () -> walletService.findById(WALLET_ID_1L, 2L));

        Assertions.assertAll(
                () -> assertEquals(ErrorCode.W002, result.getErrorCode()));
    }

    @Test
    @DisplayName("Calculate balance correctly for existing wallet")
    void findById_calculatesBalanceCorrectly() {
        // given
        Wallet wallet = TestUtils.createWalletForTest(User.builder().id(USER_ID_1L).build());
        List<FinancialTransaction> transactions = createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_1, EXPENSE_AMOUNT_1);

        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));

        mockTransactionRepositoryAndMapperBehaviour(transactions);

        // when
        WalletDTO result = walletService.findById(WALLET_ID_1L, USER_ID_1L);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(INCOME_AMOUNT_1.subtract(EXPENSE_AMOUNT_1), result.getBalance());
    }


    @Test
    @DisplayName("Should return list of walletDTOs when user has wallets")
    void findAllWallets_userHasWallets_shouldReturnListOfWallets() {
        // given
        List<Wallet> walletList = TestUtils.createWalletListForTest(3, User.builder().id(USER_ID_1L).build());
        when(walletRepository.findAllByUserIdOrderByNameAsc(USER_ID_1L)).thenReturn(walletList);

        List<WalletDTO> walletDTOS = TestUtils.createWalletDTOListForTest(3, USER_ID_1L);

        // when
        List<WalletDTO> result = walletService.findAllWallets(USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).containsExactlyInAnyOrderElementsOf(walletDTOS));

    }

    @Test
    @DisplayName("Should return an empty List when user has no wallets")
    void findAllWallets_userHasNoWallets_shouldReturnsEmptyList() {
        // given
        List<Wallet> walletList = Collections.emptyList();
        when(walletRepository.findAllByUserIdOrderByNameAsc(USER_ID_1L)).thenReturn(walletList);

        // when
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> walletService.findAllWallets(USER_ID_1L));

        // then
        Assertions.assertEquals(ErrorCode.W001, result.getErrorCode());
    }

    @Test
    @DisplayName("Should calculate balances correctly for all retrieving wallets")
    void findAllWallets_calculateBalancesCorrectly() {
        // given
        List<Wallet> walletList = TestUtils.createWalletListForTest(3, User.builder().id(USER_ID_1L).build());
        List<List<FinancialTransaction>> transactionsForAllWallets = List.of(
                createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_1, EXPENSE_AMOUNT_1),
                createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_2, EXPENSE_AMOUNT_2),
                createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_3, EXPENSE_AMOUNT_3));

        when(walletRepository.findAllByUserIdOrderByNameAsc(USER_ID_1L)).thenReturn(walletList);

        for (int i = 0; i < transactionsForAllWallets.size(); i++) {
            mockTransactionRepositoryAndMapperBehaviour(walletList.get(i), transactionsForAllWallets.get(i));
        }

        // when
        List<WalletDTO> result = walletService.findAllWallets(USER_ID_1L);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(walletList.size(), result.size());
        Assertions.assertAll(
                () -> assertEquals(INCOME_AMOUNT_1.subtract(EXPENSE_AMOUNT_1), result.get(0).getBalance()),
                () -> assertEquals(INCOME_AMOUNT_2.subtract(EXPENSE_AMOUNT_2), result.get(1).getBalance()),
                () -> assertEquals(INCOME_AMOUNT_3.subtract(EXPENSE_AMOUNT_3), result.get(2).getBalance())
        );

    }

    @Test
    @DisplayName("Should return all wallets that names partially match the given name, ignoring case")
    void findAllByNameIgnoreCase_ReturnsAllPartiallyMatchingWallets() {
        // given
        List<Wallet> walletList = TestUtils.createWalletListForTest(3, User.builder().id(USER_ID_1L).build());

        when(walletRepository.findAllByUserIdAndNameIsContainingIgnoreCase(USER_ID_1L, "wallet"))
                .thenReturn(walletList);

        List<WalletDTO> walletDTOs = TestUtils.createWalletDTOListForTest(3, USER_ID_1L);

        // when
        List<WalletDTO> result = walletService.findAllByNameIgnoreCase("wallet", USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).containsExactlyInAnyOrderElementsOf(walletDTOs));
    }

    @Test
    @DisplayName("Should calculate balances correctly for all retrieving wallets")
    void findAllByNameIgnoreCase_calculateBalanceCorrectly() {
        // given
        List<Wallet> walletList = TestUtils.createWalletListForTest(3, User.builder().id(USER_ID_1L).build());
        List<List<FinancialTransaction>> transactionsForAllWallets = List.of(
                createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_1, EXPENSE_AMOUNT_1),
                createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_2, EXPENSE_AMOUNT_2),
                createIncomeAndExpenseTransactionsForWallet(INCOME_AMOUNT_3, EXPENSE_AMOUNT_3));

        when(walletRepository.findAllByUserIdAndNameIsContainingIgnoreCase(USER_ID_1L, "wallet"))
                .thenReturn(walletList);

        for (int i = 0; i < transactionsForAllWallets.size(); i++) {
            mockTransactionRepositoryAndMapperBehaviour(walletList.get(i), transactionsForAllWallets.get(i));
        }

        // when
        List<WalletDTO> result = walletService.findAllByNameIgnoreCase("wallet", USER_ID_1L);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(walletList.size(), result.size());
        Assertions.assertAll(
                () -> assertEquals(INCOME_AMOUNT_1.subtract(EXPENSE_AMOUNT_1), result.get(0).getBalance()),
                () -> assertEquals(INCOME_AMOUNT_2.subtract(EXPENSE_AMOUNT_2), result.get(1).getBalance()),
                () -> assertEquals(INCOME_AMOUNT_3.subtract(EXPENSE_AMOUNT_3), result.get(2).getBalance())
        );


    }


    // Methods for Test purposes


    private void mockTransactionRepositoryAndMapperBehaviour(List<FinancialTransaction> transactions) {

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

    private void mockTransactionRepositoryAndMapperBehaviour(Wallet wallet, List<FinancialTransaction> transactions) {

        when(financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(wallet.getId(), USER_ID_1L))
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

    List<FinancialTransaction> createIncomeAndExpenseTransactionsForWallet(BigDecimal incomeAmount, BigDecimal expenseAmount) {
        FinancialTransaction transaction1 = TestUtils.createFinancialTransactionForTest(INCOME);
        transaction1.setAmount(incomeAmount);
        FinancialTransaction transaction2 = TestUtils.createFinancialTransactionForTest(EXPENSE);
        transaction2.setAmount(expenseAmount);

        return Arrays.asList(transaction1, transaction2);
    }

}