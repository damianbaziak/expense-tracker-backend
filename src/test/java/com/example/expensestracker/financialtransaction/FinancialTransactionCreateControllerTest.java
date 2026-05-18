package com.example.expensestracker.financialtransaction;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionService;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.impl.FinancialTransactionServiceImpl;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static java.math.BigDecimal.ONE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FinancialTransactionController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {FinancialTransactionServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class FinancialTransactionCreateControllerTest {
    private static final Long ID_1L = 1L;
    private static final Long USER_ID_1L = 1L;
    private static final String USER_EMAIL = "example@email.com";
    private static final String DESCRIPTION = "Example description_";
    private static final Long CATEGORY_ID = 1L;
    private static final Instant DATE = Instant.parse("2024-12-22T14:30:00.500Z");
    private static final BigDecimal negativeAmount = BigDecimal.valueOf(-100.00);
    private static final BigDecimal invalidAmountFormat = BigDecimal.valueOf(98.3974932);
    private static final Long WALLET_ID_1 = 1L;
    @MockBean
    private FinancialTransactionService financialTransactionService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return financial transaction and status 201-Created")
    void createFinancialTransaction_validData_shouldReturnFinancialTransactionAndStatusCreated() throws Exception {
        // given
        User user = TestUtils.createUserForTest();
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        FinancialTransactionDTO financialTransactionDTO = TestUtils.createFinancialTransactionDTOForTest(EXPENSE);

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        when(financialTransactionService.createFinancialTransaction(financialTransactionCreateDTO, USER_ID_1L))
                .thenReturn(financialTransactionDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionCreateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", CoreMatchers.is(ID_1L.intValue())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(DESCRIPTION)))
                .andExpect(jsonPath("$.type", CoreMatchers.is(EXPENSE.name())));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when financialTransactionType is null")
    void createFinancialTransaction_financialTransactionTypeNull_shouldReturnStatusBadRequest() throws Exception {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        financialTransactionCreateDTO.setType(null);
        User user = TestUtils.createUserForTest();

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionCreateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when amount is negative")
    void createFinancialTransaction_negativeAmount_shouldReturnStatusBadRequest() throws Exception {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        financialTransactionCreateDTO.setAmount(negativeAmount);
        User user = TestUtils.createUserForTest();

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionCreateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when the amount format is invalid")
    void createFinancialTransaction_invalidAmountFormat_shouldReturnStatusBadRequest() throws Exception {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        financialTransactionCreateDTO.setAmount(invalidAmountFormat);
        User user = TestUtils.createUserForTest();

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionCreateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private FinancialTransactionCreateDTO createFinancialTransactionCreateDTO() {
        return new FinancialTransactionCreateDTO(WALLET_ID_1, ONE, DESCRIPTION, EXPENSE,
                DATE, CATEGORY_ID);
    }

}