package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.FinancialTransactionController;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
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

import java.time.Instant;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static java.math.BigDecimal.ONE;

/**
 * Integration tests for the {@link FinancialTransactionController} REST controller.
 */
public class FinancialTransactionCreateIT extends IntegrationTest {
    private static final String API_URL = "/api/transactions";
    private static final String USER_EMAIL = "example@email.com";
    private static final String USER_PASSWORD = "1234567890";
    private static final Instant DATE = Instant.parse("2024-12-22T14:30:00.500Z");
    private static final String DESCRIPTION = "Example description_";
    private static final Long WALLET_ID_1 = 1L;
    @Autowired
    private FinancialTransactionRepository transactionRepository;
    @Autowired
    private FinancialTransactionCategoryRepository transactionCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void testCreateFinancialTransaction_validData() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletWithoutId(user));

        FinancialTransactionCreateDTO transactionCreateDTO = createFinancialTransactionCreateDTO();
        transactionCreateDTO.setWalletId(savedWallet.getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionCreateDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(ONE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(EXPENSE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(DESCRIPTION));

        Assertions.assertEquals(1, walletRepository.count());
        Assertions.assertEquals(1, transactionRepository.count());
    }

    @Test
    void testCreateFinancialTransaction_nonExistentWallet() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        FinancialTransactionCreateDTO transactionCreateDTO = createFinancialTransactionCreateDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionCreateDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                        String.format("Wallet with this id: %d not exist", WALLET_ID_1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage")
                        .value(ErrorCode.W001.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode")
                        .value(ErrorCode.W001.getBusinessCode()));

        Assertions.assertEquals(0, transactionRepository.count());
    }

    @Test
    void testCreateFinancialTransaction_mismatchedCategoryType_shouldReturnStatusBadRequest() throws Exception {
        final User user = createUser();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        // Creating INCOME category for createDTO, but transaction will be EXPENSE - type mismatch.
        FinancialTransactionCategory transactionCategory = transactionCategoryRepository.save(
                TestUtils.createTransactionCategoryWithoutId(INCOME, user));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletWithoutId(user));

        FinancialTransactionCreateDTO createDTO = createFinancialTransactionCreateDTO();
        createDTO.setCategoryId(transactionCategory.getId());
        createDTO.setWalletId(savedWallet.getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Authorization", "Bearer " + accessToken))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                        String.format("Financial transaction type: %s does not match financial category type: %s",
                                createDTO.getType(), transactionCategory.getType())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage")
                        .value(ErrorCode.FT002.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode")
                        .value(ErrorCode.FT002.getBusinessCode()));
    }


    // =============== These methods have been written for many tests in this file. ================

    private User createUser() {
        return User.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .build();
    }

    private FinancialTransactionCreateDTO createFinancialTransactionCreateDTO() {
        return new FinancialTransactionCreateDTO(WALLET_ID_1, ONE, DESCRIPTION, EXPENSE,
                DATE, null);


    }
}
