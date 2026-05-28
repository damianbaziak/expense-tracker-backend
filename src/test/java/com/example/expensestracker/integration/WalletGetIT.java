package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.WalletController;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration tests for the {@link WalletController} REST controller.
 */
public class WalletGetIT extends IntegrationTest {
    private static final String API_URL = "/api/wallets";
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
    private FinancialTransactionModelMapper transactionModelMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        walletRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void testGetAllByNameLikeIgnoreCase_shouldReturnWalletsWithCorrectBalance() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        List<Wallet> savedWallets = createWalletsAndSaveToDatabase(user);
        List<WalletDTO> expectedWalletDTOs = createExpectedWalletDTOS(savedWallets);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedWalletDTOs)));

        Assertions.assertEquals(3, walletRepository.count());
    }

    private List<Wallet> createWalletsAndSaveToDatabase(User user) {
        List<Wallet> wallets = TestUtils.createWalletsWithoutIDs(3, user);
        List<Wallet> savedWallets = new ArrayList<>();

        for (Wallet wallet : wallets) {
            Wallet savedWallet = walletRepository.saveAndFlush(wallet);
            createTransactionsAndSaveToDatabase(savedWallet);
            savedWallets.add(savedWallet);
        }
        return savedWallets;
    }

    private void createTransactionsAndSaveToDatabase(Wallet wallet) {
        List<FinancialTransaction> transactions = TestUtils.createTransactionsWithoutIDs(3, wallet,
                FinancialTransactionType.EXPENSE);
        for (FinancialTransaction transaction : transactions) {
            FinancialTransaction savedTransaction = transactionRepository.saveAndFlush(transaction);
        }
    }

    private List<WalletDTO> createExpectedWalletDTOS(List<Wallet> wallets) {
        return wallets.stream()
                .map(wallet -> {
                    List<FinancialTransactionDTO> transactionDTOS = transactionRepository
                            .findAllByWalletIdAndWalletUserIdOrderByDateDesc(wallet.getId(), wallet.getUser().getId())
                            .stream()
                            .map(transaction -> transactionModelMapper
                                    .mapFinancialTransactionEntityToFinancialTransactionDTO(transaction))
                            .toList();
                    BigDecimal balance = calculateCurrentBalance(transactionDTOS);
                    return new WalletDTO(wallet.getId(), wallet.getName(), wallet.getCreationDate(), wallet.getUser().getId(),
                            balance);
                })
                .toList();
    }

    private BigDecimal calculateCurrentBalance(List<FinancialTransactionDTO> transactionDTOs) {
        return transactionDTOs.stream()
                .map(transaction -> transaction.getType() == FinancialTransactionType.INCOME
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // =============== These methods have been written for many tests in this file. ================

    private User createUser() {
        return User.builder()
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .build();
    }

    private UserDetails loadUserDetailsForToken(User user) {
        String email = user.getEmail();
        return userDetailsService.loadUserByUsername(email);
    }


}
