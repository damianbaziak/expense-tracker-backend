package com.example.expensestracker;

import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.model.Wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;

public class TestUtils {

    private static final Long USER_ID_1L = 1L;
    private static final String USER_EMAIL = "example@email.com";
    private static final Long WALLET_ID_1L = 1L;
    private static final String EXAMPLE_DESCRIPTION = "Example description_";
    private static final String EXAMPLE_CATEGORY_NAME = "Example category name_";
    private static final String EXAMPLE_WALLET_NAME = "Example wallet name_";
    private static final Long ID_1L = 1L;
    private static final Long CATEGORY_ID = 1L;
    private static final Instant DATE_NOW = Instant.now();


    public static User createUserForTest() {
        return User.builder()
                .id(USER_ID_1L)
                .email(USER_EMAIL)
                .build();
    }

    public static User createUserForTest(String email) {
        return User.builder()
                .id(USER_ID_1L)
                .email(email)
                .build();
    }

    public static Wallet createWalletForTest(User user) {
        return Wallet.builder()
                .id(WALLET_ID_1L)
                .user(user)
                .name(EXAMPLE_WALLET_NAME)
                .creationDate(DATE_NOW)
                .build();

    }

    public static WalletDTO createWalletDTOForTest(Long userId) {
        return new WalletDTO(WALLET_ID_1L, EXAMPLE_WALLET_NAME, DATE_NOW, userId);
    }

    public static List<Wallet> createWalletListForTest(int count, User user) {
        ArrayList<Wallet> list = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            list.add(Wallet.builder()
                    .id(i)
                    .user(user)
                    .name(EXAMPLE_WALLET_NAME + 1)
                    .creationDate(DATE_NOW)
                    .build()
            );
        }
        return list;

    }

    public static List<WalletDTO> createWalletDTOListForTest(int count, Long userId) {
        ArrayList<WalletDTO> list = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            list.add(new WalletDTO(
                    i,
                    EXAMPLE_WALLET_NAME + 1,
                    DATE_NOW,
                    userId
            ));
        }
        return list;

    }

    public static FinancialTransaction createFinancialTransactionForTest(FinancialTransactionType type) {
        return FinancialTransaction.builder()
                .id(ID_1L)
                .amount(ONE)
                .description(EXAMPLE_DESCRIPTION)
                .type(type)
                .date(DATE_NOW)
                .build();
    }

    public static FinancialTransaction createFinancialTransactionForTest(FinancialTransactionType type,
                                                                         FinancialTransactionCategory category) {
        return FinancialTransaction.builder()
                .id(ID_1L)
                .amount(ONE)
                .description(EXAMPLE_DESCRIPTION)
                .type(type)
                .date(DATE_NOW)
                .financialTransactionCategory(category)
                .build();
    }

    public static FinancialTransactionDTO createFinancialTransactionDTOForTest(FinancialTransactionType type) {
        return new FinancialTransactionDTO(ID_1L, ONE, EXAMPLE_DESCRIPTION, type, DATE_NOW, CATEGORY_ID);
    }

    public static List<FinancialTransaction> createFinancialTransactionListForTest(
            int count, Wallet wallet, FinancialTransactionType type) {
        BigDecimal amount = new BigDecimal(100);
        ArrayList<FinancialTransaction> list = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            amount = amount.add(BigDecimal.ONE);
            list.add(FinancialTransaction.builder()
                    .id(i)
                    .wallet(wallet)
                    .type(type)
                    .amount(amount)
                    .date(DATE_NOW)
                    .description(EXAMPLE_DESCRIPTION + i)
                    .build()
            );
        }
        return list;

    }

    public static List<FinancialTransactionDTO> createFinancialTransactionDTOListForTest(
            int count, FinancialTransactionType type, Long categoryId) {
        BigDecimal amount = new BigDecimal(100);
        ArrayList<FinancialTransactionDTO> list = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            amount = amount.add(BigDecimal.ONE);
            list.add(new FinancialTransactionDTO(
                    i,
                    amount,
                    EXAMPLE_DESCRIPTION + i,
                    type,
                    DATE_NOW,
                    categoryId)
            );
        }
        return list;

    }

    public static FinancialTransactionCategory createFinancialTransactionCategoryForTest(FinancialTransactionType type) {
        return FinancialTransactionCategory.builder()
                .id(ID_1L)
                .name(EXAMPLE_CATEGORY_NAME)
                .type(type)
                .creationDate(DATE_NOW)
                .build();
    }

    public static FinancialTransactionCategoryDTO createFinancialTransactionCategoryDTOForTest(
            FinancialTransactionType type, Long userId) {
        return new FinancialTransactionCategoryDTO(ID_1L, EXAMPLE_CATEGORY_NAME, type, DATE_NOW, userId);

    }

    public static List<FinancialTransactionCategory> createFinancialTransactionCategoryListForTest(
            int count, FinancialTransactionType type, User user) {
        ArrayList<FinancialTransactionCategory> list = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            list.add(FinancialTransactionCategory.builder()
                    .id(i)
                    .name(EXAMPLE_CATEGORY_NAME + i)
                    .type(type)
                    .creationDate(DATE_NOW)
                    .user(user)
                    .build()
            );
        }
        return list;

    }

    public static List<FinancialTransactionCategoryDTO> createFinancialTransactionCategoryDTOListForTest(
            int count, FinancialTransactionType type, Long userId) {
        ArrayList<FinancialTransactionCategoryDTO> list = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            list.add(new FinancialTransactionCategoryDTO(
                    i,
                    EXAMPLE_CATEGORY_NAME + i,
                    type,
                    DATE_NOW,
                    userId
            ));
        }
        return list;
    }

}