package com.example.expensestracker.user;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.dto.UserEmailUpdateDTO;
import com.example.expensestracker.user.api.dto.UserPasswordUpdateDTO;
import com.example.expensestracker.user.api.dto.UserUsernameUpdateDTO;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.user.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, JwtService.class, MyUserDetailsService.class, JwtAuthorizationFilter.class,
                        WebSecurityConfiguration.class}),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {UserServiceImpl.class}))
class UserUpdateControllerTest {

    private static final Long ID_1L = 1L;
    private static final String EMAIL = "example@email.com";
    private static final String FIRSTNAME = "Firstname_Example";
    private static final String LASTNAME = "Lastname_Example";
    private static final String PASSWORD = "52345h.23j";
    private static final String USERNAME = "Username_Example";
    private static final String TO_LONG_USERNAME = "fdsgsDAg345_4534sdfasdfds";
    private static final String PASSWORD_1_CHAR_MORE = "0123456789.";
    private static final int AGE = 30;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status OK and userDTO with new username for valid input")
    void updateUsername_validInput_shouldReturnUserDTO() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserUsernameUpdateDTO updateDTO = new UserUsernameUpdateDTO(USERNAME);

        UserDTO userDTO = new UserDTO(
                FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, PASSWORD);

        when(userService.updateUsername(ID_1L, updateDTO, userPrincipal.getId())).thenReturn(userDTO);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/username", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.username", is(USERNAME)));
        result.andExpect(content().string(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status Bad Request when username is blank")
    void updateUsername_usernameIsBlank_shouldReturnStatusBadRequest() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserUsernameUpdateDTO updateDTO = new UserUsernameUpdateDTO("     ");

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/username", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status Bad Request when username is to long")
    void updateUsername_usernameToLong_shouldReturnStatusBadRequest() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserUsernameUpdateDTO updateDTO = new UserUsernameUpdateDTO(TO_LONG_USERNAME);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/username", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status OK and userDTO with new email for valid input")
    void updateEmail_validInput_shouldReturnUserDTO() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserEmailUpdateDTO updateDTO = new UserEmailUpdateDTO(EMAIL);

        UserDTO userDTO = new UserDTO(
                FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, PASSWORD);

        when(userService.updateEmail(ID_1L, updateDTO, userPrincipal.getId())).thenReturn(userDTO);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/email", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.email", is(EMAIL)));
        result.andExpect(content().string(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status Bad Request when email pattern is wrong")
    void updateEmail_wrongPattern_shouldReturnBadRequestStatus() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserEmailUpdateDTO updateDTO = new UserEmailUpdateDTO("wrongPattern.pl");

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/email", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status Bad Request when email is null")
    void updateEmail_emailIsNull_shouldReturnBadRequestStatus() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserEmailUpdateDTO updateDTO = new UserEmailUpdateDTO(null);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/email", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status OK and userDTO with new password for valid input")
    void updatePassword_validInput_shouldReturnUserDTO() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserPasswordUpdateDTO updateDTO = new UserPasswordUpdateDTO(PASSWORD);

        UserDTO userDTO = new UserDTO(
                FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, PASSWORD);

        when(userService.updatePassword(ID_1L, updateDTO, userPrincipal.getId())).thenReturn(userDTO);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/password", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.password", is(PASSWORD)));
        result.andExpect(content().string(objectMapper.writeValueAsString(userDTO)));
    }


    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status Bad Request when password is blank")
    void updatePassword_passwordIsBlank_shouldReturnUserDTO() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserPasswordUpdateDTO updateDTO = new UserPasswordUpdateDTO("    ");

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/password", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL)
    @DisplayName("Should return HTTP status Bad Request when password is to long")
    void updatePassword_passwordToLong_shouldReturnUserDTO() throws Exception {
        // given
        User userPrincipal = TestUtils.createUser(EMAIL);
        when(userService.findUserByEmail(EMAIL)).thenReturn(userPrincipal);

        UserPasswordUpdateDTO updateDTO = new UserPasswordUpdateDTO(PASSWORD_1_CHAR_MORE);

        // when
        ResultActions result = mockMvc.perform(patch("/api/users/{id}/password", ID_1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(updateDTO))));

        // then
        result.andExpect(status().isBadRequest());
    }




}