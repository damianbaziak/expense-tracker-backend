package com.example.expensestracker.wallet;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.dto.WalletUpdateDTO;
import com.example.expensestracker.wallet.impl.WalletServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WalletController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {WalletServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class WalletUpdateControllerTest {

    private static final String USER_EMAIL = "example@email.com";
    private static final Long USER_ID_1L = 1L;
    private static final String NEW_NAME_TO_LONG = "Example wallet name_wallet name to long";
    private static final String NEW_NAME_TO_SHORT = "W";
    private static final String NEW_WALLET_NAME = "Updated wallet name";
    private static final Long WALLET_ID_1L = 1L;


    @MockBean
    private WalletServiceImpl walletService;
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
    @DisplayName("Should return status OK and walletDTO with updated name")
    void updateWallet_withValidData_ReturnsStatusOkWithWalletDTO() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);
        WalletDTO walletDTO = TestUtils.createWalletDTO(USER_ID_1L);
        walletDTO.setName(NEW_WALLET_NAME);
        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_WALLET_NAME);

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        when(walletService.updateWallet(WALLET_ID_1L, updateDTO, USER_ID_1L)).thenReturn(walletDTO);

        // when
        ResultActions result = mockMvc.perform(patch("/api/wallets/{id}", WALLET_ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(WALLET_ID_1L.intValue())))
                .andExpect(jsonPath("$.name", is(updateDTO.getName())));

    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return status Bad Request when wallet name is to long")
    void updateWallet_walletNameToLong_ReturnsStatusBadRequest() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);

        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_NAME_TO_LONG);

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions result = mockMvc.perform(patch("/api/wallets/{id}", WALLET_ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.descriptionList[0]")
                        .value(Matchers.containsString("Name should be between 2 and 20 characters long")));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return status Bad Request when wallet name is to short")
    void updateWallet_walletNameToShort_ReturnsStatusBadRequest() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);

        WalletUpdateDTO updateDTO = new WalletUpdateDTO(NEW_NAME_TO_SHORT);

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions result = mockMvc.perform(patch("/api/wallets/{id}", WALLET_ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.descriptionList[0]").value(Matchers
                        .containsString("Name should be between 2 and 20 characters long")));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return status Bad Request when wallet name is blank")
    void updateWallet_walletNameIsBlank_ReturnsStatusBadRequest() throws Exception {
        // given
        User user = TestUtils.createUser(USER_EMAIL);

        WalletUpdateDTO updateDTO = new WalletUpdateDTO("    ");

        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        // when
        ResultActions result = mockMvc.perform(patch("/api/wallets/{id}", WALLET_ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.descriptionList[0]")
                        .value(Matchers.containsString("Name can not be blank")));
    }


}