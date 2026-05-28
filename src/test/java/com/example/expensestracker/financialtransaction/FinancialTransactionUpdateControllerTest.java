package com.example.expensestracker.financialtransaction;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionService;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionUpdateDTO;
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

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static java.math.BigDecimal.TEN;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FinancialTransactionController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {FinancialTransactionServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class FinancialTransactionUpdateControllerTest {

    private static final Long ID_1L = 1L;
    private static final Long USER_ID_1L = 1L;
    private static final String USER_EMAIL = "example@email.com";
    private static final String DESCRIPTION = "Description";
    private static final Instant DATE = Instant.parse("2024-12-22T14:30:00.500Z");
    private static final Long CATEGORY_ID = 2L;
    private static final BigDecimal negativeAmount = BigDecimal.valueOf(-100.00);
    private static final BigDecimal invalidAmountFormat = BigDecimal.valueOf(98.3974932);

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
    @DisplayName("Should return status OK and financial transaction")
    void updateFinancialTransaction_validData_shouldReturnFinancialTransactionAndStatusOK() throws Exception {
        // given
        User user = TestUtils.createUser();

        FinancialTransactionUpdateDTO ftUpdateDTO = createFinancialTransactionUpdate();

        FinancialTransactionDTO ftDTO = new FinancialTransactionDTO(
                ID_1L, TEN, DESCRIPTION, INCOME, DATE, CATEGORY_ID);

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        when(financialTransactionService.updateFinancialTransaction(ID_1L, ftUpdateDTO, USER_ID_1L)).thenReturn(ftDTO);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/transactions/{1}", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(ftUpdateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(ID_1L.intValue())))
                .andExpect(jsonPath("$.type", CoreMatchers.is(ftUpdateDTO.getType().name())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(ftUpdateDTO.getDescription())));

    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when financialTransactionType is null")
    void updateFinancialTransaction_financialTransactionTypeNull_shouldReturnsBadRequestStatus() throws Exception {
        // given
        User user = TestUtils.createUser();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        FinancialTransactionUpdateDTO financialTransactionUpdateDTO = createFinancialTransactionUpdate();
        financialTransactionUpdateDTO.setType(null);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/transactions/{id}", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionUpdateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when the amount format is invalid")
    void updateFinancialTransaction_invalidAmountFormat_shouldReturnStatusBadRequest() throws Exception {
        // given
        FinancialTransactionUpdateDTO financialTransactionUpdateDTO = createFinancialTransactionUpdate();
        financialTransactionUpdateDTO.setAmount(invalidAmountFormat);
        User user = TestUtils.createUser();

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/transactions/{id}", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionUpdateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return bad request status when amount is negative")
    void updateFinancialTransaction_negativeAmount_shouldReturnStatusBadRequest() throws Exception {
        // given
        FinancialTransactionUpdateDTO financialTransactionUpdateDTO = createFinancialTransactionUpdate();
        financialTransactionUpdateDTO.setAmount(negativeAmount);
        User user = TestUtils.createUser();

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/transactions/{id}", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(financialTransactionUpdateDTO)))
                .characterEncoding("UTF-8"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private FinancialTransactionUpdateDTO createFinancialTransactionUpdate() {
        return new FinancialTransactionUpdateDTO(TEN, DESCRIPTION, INCOME, DATE, CATEGORY_ID);
    }

}