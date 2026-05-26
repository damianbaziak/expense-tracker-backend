package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.FinancialTransactionController;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for the {@link FinancialTransactionController} REST controller.
 */
public class FinancialTransactionGetIT extends IntegrationTest {
    private static final String API_URL = "/api/transactions";
    private static final String USER_EMAIL = "example@email.com";
    private static final String USER_PASSWORD = "1234567890";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FinancialTransactionRepository transactionRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void testGetTransactionByID_shouldReturnTransactionAndStatusOK() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletForTestWithoutId(user));

        FinancialTransaction financialTransaction =
                TestUtils.createTransactionForTestWithoutId(FinancialTransactionType.EXPENSE);
        financialTransaction.setWallet(savedWallet);

        FinancialTransaction savedFinancialTransaction = transactionRepository.save(financialTransaction);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/{id}", savedFinancialTransaction.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedFinancialTransaction.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(financialTransaction.getType().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount")
                        .value(BigDecimal.valueOf(1.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(financialTransaction.getDescription()));

        Assertions.assertEquals(1, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }

    @Test
    void testGetTransactionByID_transactionNotExist_shouldReturnStatusNotFound() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletForTestWithoutId(user));

        FinancialTransaction financialTransaction =
                TestUtils.createTransactionForTestWithoutId(FinancialTransactionType.EXPENSE);
        financialTransaction.setWallet(savedWallet);
        transactionRepository.save(financialTransaction);

        // when and then
        // Given transaction with ID:123 doesn't exist
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/{id}", 123)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(String.format(
                        "Financial transaction with id: %d does not found", 123)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode").value(
                        ErrorCode.FT001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(
                        ErrorCode.FT001.getBusinessMessage()));

        Assertions.assertEquals(1, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }

    @Test
    void testGetTransactionsByWalletID_shouldReturnStatusOK() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletForTestWithoutId(user));

        List<FinancialTransaction> savedTransactions = saveTransactionsToDatabase(savedWallet);

        // when and then
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders.get(API_URL).param("walletId", String.valueOf(savedWallet.getId()))
                                .header("Authorization", "Bearer " + accessToken))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(savedTransactions.size()))
                        .andExpect(jsonPath("$[0].amount").value(
                                savedTransactions.get(0).getAmount().doubleValue()))
                        .andExpect(jsonPath("$[1].amount").value(
                                savedTransactions.get(1).getAmount().doubleValue()))
                        .andExpect(jsonPath("$[2].amount").value(
                                savedTransactions.get(2).getAmount().doubleValue()))
                        .andExpect(jsonPath("$[0].description").value(savedTransactions.get(0).getDescription()))
                        .andExpect(jsonPath("$[1].description").value(savedTransactions.get(1).getDescription()))
                        .andExpect(jsonPath("$[2].description").value(savedTransactions.get(2).getDescription()));

        Assertions.assertEquals(3, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }

    @Test
    void testGetTransactionsByWalletID_walletNotExists_shouldReturnStatusOK() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletForTestWithoutId(user));

        saveTransactionsToDatabase(savedWallet);

        // when and then
        // Wallet whit id:123
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders.get(API_URL).param("walletId", String.valueOf(123))
                                .header("Authorization", "Bearer " + accessToken))
                        .andExpect(MockMvcResultMatchers.status().isNotFound())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                                String.format("Wallet with this id: %d not exist", 123)
                        ))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(
                                ErrorCode.W001.getBusinessMessage()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode").value(
                                ErrorCode.W001.getBusinessCode()));

        Assertions.assertEquals(3, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }


    // =============== These methods have been written for many tests in this file. ================

    private User createUser() {
        return User.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .build();
    }

    private List<FinancialTransaction> saveTransactionsToDatabase(Wallet wallet) {
        List<FinancialTransaction> financialTransactions =
                TestUtils.createTransactionsForTestWithoutIDs(3, wallet, FinancialTransactionType.EXPENSE);

        for (FinancialTransaction financialTransaction : financialTransactions) {
            transactionRepository.save(financialTransaction);
        }
        return financialTransactions;
    }

}
