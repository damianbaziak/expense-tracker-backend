package com.example.trainingsapp.authorization;

import com.example.trainingsapp.authorization.api.AuthService;
import com.example.trainingsapp.authorization.api.MyUserDetailsService;
import com.example.trainingsapp.authorization.impl.AuthServiceImpl;
import com.example.trainingsapp.authorization.webtoken.JwtService;
import com.example.trainingsapp.general.exception.ErrorStrategy;
import com.example.trainingsapp.user.api.UserRepository;
import com.example.trainingsapp.user.api.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthServiceImpl.class),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class AuthRegisterControllerTest {

    private static final String EMAIL = "example@email.com";
    private static final String FIRSTNAME = "Example firstname";
    private static final String LASTNAME = "example@email.com";
    private static final Integer AGE = 20;
    private static final String USERNAME = "Example username";
    private static final String PASSWORD = "String10ch";
    private static final String USERNAME_1_CHAR_MORE = "fdsgsDAg345_4534m";
    private static final String PASSWORD_1_CHAR_MORE = "0123456789.";
    private static final String PASSWORD_WRONG_PATTERN = "§$fdg,,&/%$§";

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static Stream<Arguments> provideInvalidFirstnames() {
        return Stream.of(
                Arguments.of(new UserDTO("F", LASTNAME, AGE, EMAIL, USERNAME, PASSWORD),
                        400, "firstname must have min 2 characters"),
                Arguments.of(new UserDTO(null, LASTNAME, AGE, EMAIL, USERNAME, PASSWORD),
                        400, "firstname is mandatory"),
                Arguments.of(new UserDTO("    ", LASTNAME, AGE, EMAIL, USERNAME, PASSWORD),
                        400, "firstname is mandatory")
        );
    }

    public static Stream<Arguments> provideInvalidLastnames() {
        return Stream.of(
                Arguments.of(new UserDTO(FIRSTNAME, "L", AGE, EMAIL, USERNAME, PASSWORD),
                        400, "lastname must have min 2 characters"),
                Arguments.of(new UserDTO(FIRSTNAME, null, AGE, EMAIL, USERNAME, PASSWORD),
                        400, "lastname is mandatory"),
                Arguments.of(new UserDTO(FIRSTNAME, "    ", AGE, EMAIL, USERNAME, PASSWORD),
                        400, "lastname is mandatory")
        );
    }

    private static Stream<Arguments> provideInvalidUsernames() {
        return Stream.of(
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME_1_CHAR_MORE, PASSWORD),
                        400, "username must be between 2 and 16 characters"),
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, "A", PASSWORD),
                        400, "username must be between 2 and 16 characters"),
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, null, PASSWORD),
                        400, "username is mandatory"),
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, "    ", PASSWORD),
                        400, "username is mandatory")
        );
    }

    private static Stream<Arguments> provideInvalidPasswords() {
        return Stream.of(
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, PASSWORD_1_CHAR_MORE),
                        400, "Password muss contain exactly 10 characters and can contain only letters and digits"),
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, "PAS"),
                        400, "Password muss contain exactly 10 characters and can contain only letters and digits"),
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, PASSWORD_WRONG_PATTERN),
                        400, "Password muss contain exactly 10 characters and can contain only letters and digits"),
                Arguments.of(new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL, USERNAME, null),
                        400, "password is mandatory")
        );
    }

    @Test
    @DisplayName("Should return status Created - 201 and user DTO when credentials are valid")
    void registerUser_validCredentials_shouldReturnStatus201() throws Exception {
        // given
        UserDTO userDTO = new UserDTO(FIRSTNAME, LASTNAME, AGE, EMAIL,
                USERNAME, PASSWORD);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().isCreated());
        result.andExpect(content().string("User created correctly"));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidFirstnames")
    @DisplayName("Should return status 400 BadRequest and correct error message when firstname is invalid")
    void registerUser_invalidFirstname_returnsBadRequestAndErrorMessage(
            UserDTO userDTO, int expectedStatus, String expectedMessage) throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().is(expectedStatus));
        result.andExpect(jsonPath("$.firstname").value(expectedMessage));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidLastnames")
    @DisplayName("Should return status 400 BadRequest and correct error message when lastname is invalid")
    void registerUser_invalidLastname_returnsBadRequestAndErrorMessage(
            UserDTO userDTO, int expectedStatus, String expectedMessage) throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().is(expectedStatus));
        result.andExpect(jsonPath("$.lastname").value(expectedMessage));

    }


    @Test
    @DisplayName("Should return status 400 BadRequest when age is above the maximum limit")
    void registerUser_ageTooHigh_shouldReturnStatusBadRequest() throws Exception {
        // given
        UserDTO userDTO = new UserDTO(FIRSTNAME, LASTNAME, 61, EMAIL,
                USERNAME, PASSWORD);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return status 400 BadRequest when age is below the minimum limit")
    void registerUser_ageTooLow_shouldReturnStatusBadRequest() throws Exception {
        // given
        UserDTO userDTO = new UserDTO(FIRSTNAME, LASTNAME, 15, EMAIL,
                USERNAME, PASSWORD);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return status 400 BadRequest when email is blank")
    void registerUser_emailIsBlank_shouldReturnStatusBadRequest() throws Exception {
        // given
        UserDTO userDTO = new UserDTO(FIRSTNAME, LASTNAME, AGE, "    ",
                USERNAME, PASSWORD);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return status 400 BadRequest and correct error message for wrong email pattern")
    void registerUser_invalidEmailPattern_returnsBadRequestAndErrorMessage() throws Exception {
        // given
        UserDTO userDTO = new UserDTO(FIRSTNAME, LASTNAME, AGE, "wrongEmail.com",
                USERNAME, PASSWORD);

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.email").value("invalid email address"));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsernames")
    @DisplayName("Should return status 400 BadRequest and correct error message when username is invalid")
    void registerUser_invalidUsername_returnsBadRequestAndErrorMessage(
            UserDTO userDTO, int expectedStatus, String expectedMessage) throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().is(expectedStatus));
        result.andExpect(jsonPath("$.username").value(expectedMessage));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidPasswords")
    @DisplayName("Should return status 400 BadRequest and correct error message when password is invalid")
    void registerUser_invalidPassword_returnsBadRequestAndErrorMessage(
            UserDTO userDTO, int expectedStatus, String expectedMessage) throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        // then
        result.andExpect(status().is(expectedStatus));
        result.andExpect(jsonPath("$.password").value(expectedMessage));

    }


}