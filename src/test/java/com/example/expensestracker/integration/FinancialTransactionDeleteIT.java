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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Integration tests for the {@link FinancialTransactionController} REST controller.
 */
public class FinancialTransactionDeleteIT extends IntegrationTest {
    private static final String API_URL = "/api/transactions";
    private static final String USER_EMAIL = "example@email.com";
    private static final String USER_PASSWORD = "1234567890";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private FinancialTransactionRepository transactionRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    public void testDeleteTransactionById() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        Wallet savedWallet = createWalletAndSaveToDatabase(user);
        FinancialTransaction financialTransaction = TestUtils.createTransactionForTestWithoutId(savedWallet,
                FinancialTransactionType.INCOME);
        FinancialTransaction savedTransaction = transactionRepository.save(financialTransaction);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/{id}", savedTransaction.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertEquals(0, transactionRepository.count());
        Assertions.assertEquals(1, walletRepository.count());
    }

    @Test
    public void testDeleteTransactionById_invalidIdGiven() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        Wallet savedWallet = createWalletAndSaveToDatabase(user);
        FinancialTransaction financialTransaction = TestUtils.createTransactionForTestWithoutId(savedWallet,
                FinancialTransactionType.INCOME);
        transactionRepository.save(financialTransaction);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/{id}", 111) //<- Invalid Id
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(
                        ErrorCode.FT001.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode").value(
                        ErrorCode.FT001.getBusinessCode()));

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

    private Wallet createWalletAndSaveToDatabase(User user) {
        Wallet wallet = TestUtils.createWalletForTestWithoutId(user);
        return walletRepository.save(wallet);
    }

    private UserDetails loadUserDetailsForToken(User user) {
        String email = user.getEmail();
        return userDetailsService.loadUserByUsername(email);
    }
}
