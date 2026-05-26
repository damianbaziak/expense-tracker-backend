package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.financialtransaktioncategory.FinancialTransactionCategoryController;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryCreateDTO;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import org.hamcrest.Matchers;
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

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;

/**
 * Integration tests for the {@link FinancialTransactionCategoryController} REST controller.
 */
public class FinancialTransactionCategoryCreateIT extends IntegrationTest {
    private static final String FINANCIAL_TRANSACTION_CATEGORY_URL = "/api/categories";
    private static final String USER_PASSWORD = "01234567890";
    private static final String USER_EMAIL = "example@email.com";
    private static final String EXAMPLE_CATEGORY_NAME = "Example category name_";
    private static final String CATEGORY_NAME_TO_LONG = "sdfasdfas4353432523m45bn4m5nbmnbm2345234";

    @Autowired
    private FinancialTransactionCategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateCategory_validData() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        FinancialTransactionCategoryCreateDTO financialTransactionCategoryCreateDTO =
                createCategoryCreateDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(FINANCIAL_TRANSACTION_CATEGORY_URL)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(financialTransactionCategoryCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(EXAMPLE_CATEGORY_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(
                        FinancialTransactionType.INCOME.toString()));

        Assertions.assertEquals(1, categoryRepository.count());
    }

    @Test
    public void testCreateCategory_nameExceedingMaxLengthGiven() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        FinancialTransactionCategoryCreateDTO categoryCreateDTO
                = new FinancialTransactionCategoryCreateDTO(CATEGORY_NAME_TO_LONG, EXPENSE);

        mockMvc.perform(MockMvcRequestBuilders.post(FINANCIAL_TRANSACTION_CATEGORY_URL)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(ErrorCode.TEA001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage")
                        .value(ErrorCode.TEA001.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descriptionList[0]").value(
                        Matchers.containsString("Name size too long")));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void testCreateCategory_emptyNameGiven() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        FinancialTransactionCategoryCreateDTO categoryCreateDTO
                = new FinancialTransactionCategoryCreateDTO("     ", EXPENSE);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.post(FINANCIAL_TRANSACTION_CATEGORY_URL)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(ErrorCode.TEA001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage")
                        .value(ErrorCode.TEA001.getBusinessMessage()));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void testCreateCategory_nullTypeGiven() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        FinancialTransactionCategoryCreateDTO categoryCreateDTO
                = new FinancialTransactionCategoryCreateDTO(EXAMPLE_CATEGORY_NAME, null);

        // when and then
        mockMvc.perform(MockMvcRequestBuilders.post(FINANCIAL_TRANSACTION_CATEGORY_URL)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(ErrorCode.TEA001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage")
                        .value(ErrorCode.TEA001.getBusinessMessage()));

        Assertions.assertEquals(0, categoryRepository.count());
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

    private FinancialTransactionCategoryCreateDTO createCategoryCreateDTO() {
        return new FinancialTransactionCategoryCreateDTO(EXAMPLE_CATEGORY_NAME, FinancialTransactionType.INCOME);
    }

}
