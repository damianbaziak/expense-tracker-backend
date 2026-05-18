package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.FinancialTransactionController;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
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
public class FinancialTransactionIT extends IntegrationTest {

    private static final String USER_EMAIL = "example@email.com";

    private static final String USER_PASSWORD = "1234567890";

    private static final Long ID_1L = 1L;

    private static final String API_URL = "/api/transactions";

    private static final Long CATEGORY_ID = 1L;

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

        User user = userRepository.save(
                User.builder()
                        .email(USER_EMAIL)
                        .password(USER_PASSWORD)
                        .build());

        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletForTest(user));

        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        financialTransactionCreateDTO.setWalletId(savedWallet.getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(financialTransactionCreateDTO))
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

        User user = userRepository.save(
                User.builder()
                        .email(USER_EMAIL)
                        .password(USER_PASSWORD)
                        .build());

        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(financialTransactionCreateDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                        String.format("Wallet with this id: %d not exist", WALLET_ID_1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ErrorCode.W001.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode")
                        .value(ErrorCode.W001.getBusinessCode()));

        Assertions.assertEquals(0, transactionRepository.count());
    }

    @Test
    void testCreateFinancialTransaction_mismatchedCategoryType_shouldReturnStatusBadRequest() throws Exception {

        User user = userRepository.save(User.builder().email(USER_EMAIL).password(USER_PASSWORD).build());

        String accessToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        // // Creating INCOME category, but transaction will be EXPENSE - type mismatch
        FinancialTransactionCategory financialTransactionCategory = transactionCategoryRepository.save(
                TestUtils.createFinancialTransactionCategoryForTest(INCOME, user));

        Wallet savedWallet = walletRepository.save(TestUtils.createWalletForTest(user));

        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        financialTransactionCreateDTO.setCategoryId(financialTransactionCategory.getId());
        financialTransactionCreateDTO.setWalletId(savedWallet.getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(financialTransactionCreateDTO))
                        .header("Authorization", "Bearer " + accessToken))

                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                        String.format("Financial transaction type: %s does not match financial category type: %s",
                                financialTransactionCreateDTO.getType(), financialTransactionCategory.getType())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ErrorCode.FT002.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode")
                        .value(ErrorCode.FT002.getBusinessCode()));

    }


    private FinancialTransactionCreateDTO createFinancialTransactionCreateDTO() {
        return new FinancialTransactionCreateDTO(WALLET_ID_1, ONE, DESCRIPTION, EXPENSE,
                DATE, null);


    }
}
