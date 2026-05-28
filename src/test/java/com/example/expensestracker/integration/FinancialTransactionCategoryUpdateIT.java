package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.financialtransaktioncategory.FinancialTransactionCategoryController;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryUpdateDTO;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Integration tests for the {@link FinancialTransactionCategoryController} REST controller.
 */
public class FinancialTransactionCategoryUpdateIT extends IntegrationTest {
    private static final String FINANCIAL_TRANSACTION_CATEGORY_URL = "/api/categories";
    private static final String USER_PASSWORD = "01234567890";
    private static final String USER_EMAIL = "example@email.com";
    private static final String EXAMPLE_CATEGORY_NAME = "Example category name_";
    private static final String NEW_CATEGORY_NAME = "New_category_name";
    private static final FinancialTransactionType NEW_CATEGORY_TRANSACTION_TYPE = FinancialTransactionType.INCOME;
    @Autowired
    private FinancialTransactionCategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testUpdateFinancialTransactionCategory_validData() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        FinancialTransactionCategory category = createCategoryAndSaveToDatabase(user);
        FinancialTransactionCategoryUpdateDTO categoryUpdateDTO = createCategoryUpdateDTO();

        mockMvc.perform(MockMvcRequestBuilders.patch(FINANCIAL_TRANSACTION_CATEGORY_URL + "/{id}", category.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryUpdateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NEW_CATEGORY_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(NEW_CATEGORY_TRANSACTION_TYPE.toString()));

        Assertions.assertEquals(1, categoryRepository.count());
    }

    @Test
    void testUpdateFinancialTransactionCategory_invalidIdGiven_shouldReturnStatusNotFound() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        createCategoryAndSaveToDatabase(user);
        FinancialTransactionCategoryUpdateDTO categoryUpdateDTO = createCategoryUpdateDTO();

        mockMvc.perform(MockMvcRequestBuilders.patch(FINANCIAL_TRANSACTION_CATEGORY_URL + "/{id}",
                                123) // <- Not Existent Id
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryUpdateDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode").value(ErrorCode.FTC001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(
                        ErrorCode.FTC001.getBusinessMessage()));

        Assertions.assertEquals(1, categoryRepository.count());
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

    private FinancialTransactionCategory createCategoryAndSaveToDatabase(User user) {
        FinancialTransactionCategory category = TestUtils.createTransactionCategoryWithoutId(
                FinancialTransactionType.EXPENSE, user);
        categoryRepository.save(category);
        return category;
    }

    private FinancialTransactionCategoryUpdateDTO createCategoryUpdateDTO() {
        return new FinancialTransactionCategoryUpdateDTO(NEW_CATEGORY_NAME, NEW_CATEGORY_TRANSACTION_TYPE);
    }
}
