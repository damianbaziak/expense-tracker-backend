package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.FinancialTransactionController;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionUpdateDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.Instant;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static java.math.BigDecimal.ONE;

/**
 * Integration tests for the {@link FinancialTransactionController} REST controller.
 */
public class FinancialTransactionUpdateIT extends IntegrationTest {
    private static final String API_URL = "/api/transactions";
    private static final String USER_EMAIL = "example@email.com";
    private static final String USER_PASSWORD = "1234567890";
    private static final Instant DATE = Instant.parse("2024-12-22T14:30:00.500Z");
    private static final String DESCRIPTION = "Example description_";
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
    void testUpdateTransactionById_validData() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletWithoutId(user));

        FinancialTransaction savedTransaction = transactionRepository.save(createFinancialTransaction(savedWallet));

        FinancialTransactionUpdateDTO updateDTO = createFinancialTransactionUpdateDTO();

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/{id}", savedTransaction.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(ONE.doubleValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(DESCRIPTION))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value(DATE.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(EXPENSE.name()));

        Assertions.assertEquals(1, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }

    @Test
    void testUpdateTransactionById_invalidIdGiven_shouldReturnStatusNotFound() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletWithoutId(user));

        // Transaction saved with a custom ID.
        transactionRepository.save(createFinancialTransaction(savedWallet));

        FinancialTransactionUpdateDTO updateDTO = createFinancialTransactionUpdateDTO();

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/{id}", 123) //<- Invalid ID
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode").value(ErrorCode.FT001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(ErrorCode.FT001.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                        String.format("Financial transaction with this id: %d not exist", 123)
                ));

        Assertions.assertEquals(1, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }


    // =============== These methods have been written for many tests in this file. ================

    private User createUser() {
        return User.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .build();
    }


    private FinancialTransactionUpdateDTO createFinancialTransactionUpdateDTO() {
        return FinancialTransactionUpdateDTO.builder()
                .amount(ONE)
                .description(DESCRIPTION)
                .type(EXPENSE)
                .date(DATE)
                .build();
    }

    // Create a transaction to save to the database with values that will be updated.
    private FinancialTransaction createFinancialTransaction(Wallet wallet) {
        return FinancialTransaction.builder()
                .amount(BigDecimal.TWO)
                .description("Old description")
                .type(INCOME)
                .date(Instant.now())
                .wallet(wallet)
                .build();
    }

}
