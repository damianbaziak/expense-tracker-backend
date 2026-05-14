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
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.impl.WalletServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {WalletServiceImpl.class}),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class WalletGetControllerTest {

    private static final String USER_EMAIL = "example@email.com";
    private static final Long USER_ID_1L = 1L;
    private static final Long WALLET_ID_1L = 1L;
    private static final String NO_WALLETS_DESCRIPTION = "You have no wallets";
    private static final String WALLET_NOT_EXIST_DESCRIPTION = "Wallet not exists";
    private static final String PARTIALLY_WALLET_NAME = "walle";

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
    @DisplayName("Should return HTTP status OK and a list of wallet DTOs when wallets exist")
    void getWallets_walletsExist_shouldReturnWalletsList() throws Exception {
        // given
        User user = TestUtils.createUserForTest(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        List<WalletDTO> walletDTOS = TestUtils.createWalletDTOListForTest(3, USER_ID_1L);

        when(walletService.findAllWallets(USER_ID_1L)).thenReturn(walletDTOS);

        // when
        ResultActions result = mockMvc.perform(get("/api/wallets"));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(walletDTOS.size()));
        result
                .andExpect(jsonPath("$[0].id").value(walletDTOS.get(0).getId().intValue()))
                .andExpect(jsonPath("$[0].name").value(walletDTOS.get(0).getName()))
                .andExpect(jsonPath("$[1].id").value(walletDTOS.get(1).getId().intValue()))
                .andExpect(jsonPath("$[1].name").value(walletDTOS.get(1).getName()))
                .andExpect(jsonPath("$[2].id").value(walletDTOS.get(2).getId().intValue()))
                .andExpect(jsonPath("$[2].name").value(walletDTOS.get(2).getName()));

    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status Not Found and a string as a body")
    void getWallets_userHasNoWallets_shouldReturnNotFoundStatus() throws Exception {
        // given
        User user = TestUtils.createUserForTest(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        doThrow(new AppRuntimeException(ErrorCode.W001, NO_WALLETS_DESCRIPTION)).when(walletService)
                .findAllWallets(USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(get("/api/wallets"));

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorCode.W001.getBusinessMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(NO_WALLETS_DESCRIPTION));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status OK and a wallet DTO when wallet exists")
    void getWalletById_whenWalletExists_shouldReturnWalletDTO() throws Exception {
        // given
        User user = TestUtils.createUserForTest(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        WalletDTO walletDTO = TestUtils.createWalletDTOForTest(USER_ID_1L);
        when(walletService.findById(WALLET_ID_1L, USER_ID_1L)).thenReturn(walletDTO);

        // when
        ResultActions result = mockMvc.perform(get("/api/wallets/{id}", WALLET_ID_1L));

        // then
        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("$.id").value(WALLET_ID_1L))
                .andExpect(jsonPath("$.name").value(walletDTO.getName()))
                .andExpect(jsonPath("$.creationDate").value(walletDTO.getCreationDate().toString()))
                .andExpect(jsonPath("$.userId").value(USER_ID_1L));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status Not Found and string as a body when wallet not exists")
    void getWalletById_walletNotExists_shouldReturnNotFoundStatus() throws Exception {
        // given
        User user = TestUtils.createUserForTest(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        doThrow(new AppRuntimeException(ErrorCode.W001, WALLET_NOT_EXIST_DESCRIPTION))
                .when(walletService).findById(WALLET_ID_1L, USER_ID_1L);

        // when
        ResultActions result = mockMvc.perform(get("/api/wallets/{id}", WALLET_ID_1L));

        // then
        result.andExpect(status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(WALLET_NOT_EXIST_DESCRIPTION));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorCode.W001.getBusinessMessage()));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP OK and wallets matching the partial name")
    void getAllByNameLikeIgnoreCase_matchingWalletsExist_ShouldReturnThem() throws Exception {
        // given
        User user = TestUtils.createUserForTest(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        List<WalletDTO> walletDTOS = TestUtils.createWalletDTOListForTest(3, USER_ID_1L);

        when(walletService.findAllByNameIgnoreCase(PARTIALLY_WALLET_NAME, USER_ID_1L)).thenReturn(walletDTOS);

        // when
        ResultActions result = mockMvc.perform(get("/api/wallets/wallets/{name}", PARTIALLY_WALLET_NAME));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(walletDTOS.size()));
        result
                .andExpect(jsonPath("$[0].id").value(walletDTOS.get(0).getId().intValue()))
                .andExpect(jsonPath("$[0].name").value(containsString(PARTIALLY_WALLET_NAME)))
                .andExpect(jsonPath("$[1].id").value(walletDTOS.get(1).getId().intValue()))
                .andExpect(jsonPath("$[1].name").value(containsString(PARTIALLY_WALLET_NAME)))
                .andExpect(jsonPath("$[2].id").value(walletDTOS.get(2).getId().intValue()))
                .andExpect(jsonPath("$[2].name").value(containsString(PARTIALLY_WALLET_NAME)));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status No Content and an empty list")
    void getAllByNameLikeIgnoreCase_matchingWalletsNotExist_shouldReturnEmptyList() throws Exception {
        // given
        User user = TestUtils.createUserForTest(USER_EMAIL);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

        when(walletService.findAllByNameIgnoreCase(PARTIALLY_WALLET_NAME, USER_ID_1L))
                .thenReturn(Collections.emptyList());

        // when
        ResultActions result = mockMvc.perform(get("/api/wallets/wallets/{name}", PARTIALLY_WALLET_NAME));

        // then
        result.andExpect(status().isNoContent());
        result.andExpect(jsonPath("$.size()").value(0));
    }
}