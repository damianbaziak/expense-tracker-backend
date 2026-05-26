package com.example.expensestracker.financialtransaktioncategory;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryService;
import com.example.expensestracker.financialtransaktioncategory.impl.FinancialTransactionCategoryServiceImpl;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(controllers = FinancialTransactionCategoryController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {FinancialTransactionCategoryServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class FinancialTransactionCategoryDeleteControllerTest {
    private static final Long ID_1L = 1L;
    private static final Long ID_0L = 0L;
    private static final Long USER_ID_1L = 1L;
    private static final String USER_EMAIL = "example@email.com";
    @MockBean
    private FinancialTransactionCategoryService financialTransactionCategoryService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should returns status OK and String as a body when category deleted correctly")
    @WithMockUser(username = USER_EMAIL)
    void deleteFinancialTransactionCategory_correctDeletion() throws Exception {
        // given
        User user = TestUtils.createUserForTest();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        doNothing().when(financialTransactionCategoryService).deleteCategory(ID_1L, USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/categories/{id}", ID_1L));

        // then
        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Financial transaction category deleted successfully"));
        verify(financialTransactionCategoryService, times(1)).deleteCategory(1L, 1L);
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return status 404 when category ID not exist")
    void deleteCategoryById_categoryNotExist_shouldReturnStatusNotFound() throws Exception {
        // given
        User user = TestUtils.createUserForTest();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        doThrow(new AppRuntimeException(ErrorCode.FTC001, "Category not found")).when(
                financialTransactionCategoryService).deleteCategory(ID_1L, USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/categories/{id}", ID_1L));

        // then
        result
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(ErrorCode.FTC001.getBusinessMessage()));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when category ID is zero")
    void deleteCategoryById_categoryIdIsZero_shouldReturnBadRequestStatus() throws Exception {
        // given
        User user = TestUtils.createUserForTest();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions result = mockMvc.perform(delete("/api/categories/{id}", ID_0L));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.jsonPath("$.businessCode").value(ErrorCode.TEA001.getBusinessCode()),
                MockMvcResultMatchers.jsonPath("$.businessMessage").value(ErrorCode.TEA001.getBusinessMessage()),
                MockMvcResultMatchers.jsonPath("$.statusCode").value(ErrorCode.TEA001.getHttpStatus()));
    }
}