package com.example.expensestracker.wallet;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.impl.WalletServiceImpl;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {WalletServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class WalletDeleteControllerTest {

    private static final String USER_EMAIL = "example@email.com";
    private static final Long USER_ID_1L = 1L;
    private static final Long WALLET_ID_1L = 1L;
    private static final Long ID_0 = 0L;

    @MockBean
    private WalletServiceImpl walletService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return status OK and string as a body when wallet deleted correctly")
    @WithMockUser(username = USER_EMAIL)
    void deleteWalletById_correctDeletion() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        doNothing().when(walletService).deleteWallet(WALLET_ID_1L, USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/wallets/{1}", WALLET_ID_1L));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet deleted successfully"));
        verify(walletService, times(1)).deleteWallet(WALLET_ID_1L, USER_ID_1L);
    }

    @Test
    @DisplayName("Should return status 404 - not found and string as a body when wallet not exists")
    @WithMockUser(username = USER_EMAIL)
    void deleteWalletById_walletNotExists_shouldReturnStatusNotFound() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        doThrow(new AppRuntimeException(ErrorCode.W001, "Wallet not exists"))
                .when(walletService).deleteWallet(WALLET_ID_1L, USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(delete("/api/wallets/{1}", WALLET_ID_1L));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Wallet not exists"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.businessMessage").value(ErrorCode.W001.getBusinessMessage()));
    }

    @Test
    @DisplayName("Should return status Bad Request")
    @WithMockUser(username = USER_EMAIL)
    void deleteWalletById_walletIdIsZero_shouldReturnStatusBadRequest() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions result = mockMvc.perform(delete("/api/wallets/{id}", ID_0));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.businessCode", is(ErrorCode.TEA001.getBusinessCode())))
                .andExpect(jsonPath("$.businessMessage", is(ErrorCode.TEA001.getBusinessMessage())))
                .andExpect(jsonPath("$.statusCode", is(ErrorCode.TEA001.getHttpStatus())));

    }


}