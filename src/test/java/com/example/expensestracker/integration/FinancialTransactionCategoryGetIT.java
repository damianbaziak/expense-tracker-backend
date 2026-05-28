package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.financialtransaktioncategory.FinancialTransactionCategoryController;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
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

import java.util.List;

/**
 * Integration tests for the {@link FinancialTransactionCategoryController} REST controller.
 */
public class FinancialTransactionCategoryGetIT extends IntegrationTest {
    private static final String API_URL = "/api/categories";
    private static final String USER_PASSWORD = "01234567890";
    private static final String USER_EMAIL = "example@email.com";
    private static final String EXAMPLE_CATEGORY_NAME = "Example category name_";
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
    public void testGetTransactionCategoryById_validData() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        FinancialTransactionCategory category = createCategoryAndSaveToDatabase(user);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/{id}",
                                category.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialTransactionCounter").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialTransactionCategoryDTO.name").value(
                        EXAMPLE_CATEGORY_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialTransactionCategoryDTO.type").value(
                        FinancialTransactionType.EXPENSE.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialTransactionCategoryDTO.userId").value(
                        user.getId()));

        Assertions.assertEquals(1, categoryRepository.count());
    }

    @Test
    public void testGetTransactionCategoryById_invalidIdGiven() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        createCategoryAndSaveToDatabase(user);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/{id}",
                                123) // <- Invalid Id
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessCode").value(
                        ErrorCode.FTC001.getBusinessCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(
                        ErrorCode.FTC001.getBusinessMessage()));

        Assertions.assertEquals(1, categoryRepository.count());
    }

    @Test
    public void testGetTransactionCategories_shouldReturnStatusOK() throws Exception {
        User user = createUser();
        userRepository.save(user);
        UserDetails userDetails = loadUserDetailsForToken(user);
        String accessToken = jwtService.generateToken(userDetails);

        List<FinancialTransactionCategory> transactionCategories = createAndSaveTransactionCategoriesToDatabase(user);
        List<FinancialTransactionCategoryDTO> expectedCategories
                = createExpectedTransactionCategoriesDTOS(transactionCategories);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedCategories)));

        Assertions.assertEquals(3, categoryRepository.count());
    }

    private List<FinancialTransactionCategory> createAndSaveTransactionCategoriesToDatabase(User user) {
        List<FinancialTransactionCategory> transactionCategories =
                TestUtils.createTransactionCategoriesWithoutIDs(3, FinancialTransactionType.EXPENSE, user);
        for (FinancialTransactionCategory transactionCategory : transactionCategories) {
            categoryRepository.save(transactionCategory);
        }
        return transactionCategories;
    }

    private List<FinancialTransactionCategoryDTO> createExpectedTransactionCategoriesDTOS(
            List<FinancialTransactionCategory> transactionCategories) {
        return transactionCategories.stream()
                .map(financialTransactionCategory -> new FinancialTransactionCategoryDTO(
                        financialTransactionCategory.getId(), financialTransactionCategory.getName(),
                        financialTransactionCategory.getType(), financialTransactionCategory.getCreationDate(),
                        financialTransactionCategory.getUser().getId()))
                .toList();
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

}
