package com.example.trainingsapp.authorization;

import com.example.trainingsapp.authorization.api.MyUserDetailsService;
import com.example.trainingsapp.authorization.api.dto.AuthAccessTokenDTO;
import com.example.trainingsapp.authorization.api.dto.UserLoginDTO;
import com.example.trainingsapp.authorization.impl.AuthServiceImpl;
import com.example.trainingsapp.authorization.webtoken.JwtService;
import com.example.trainingsapp.general.exception.ErrorStrategy;
import com.example.trainingsapp.user.api.UserRepository;
import com.example.trainingsapp.user.api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class, AuthServiceImpl.class}))
@ActiveProfiles("test")
class AuthLoginControllerTest {

    private static final String EMAIL = "example@email.com";
    private static final String NOT_EXISTING_EMAIL = "email@notPresent.com";
    private static final String EMAIL_WRONG_PATTERN = "email@$(%/)(.com";
    private static final String PASSWORD = "String10ch";
    private static final String WRONG_PASSWORD = "WrongPas10";
    private static final String PASSWORD_1_CHAR_MORE = "String11cha";
    private static final String PASSWORD_WRONG_PATTERN = "§$fdg,,&/%$§";

    @MockBean
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static Stream<Arguments> provideInvalidPasswords() {
        return Stream.of(
                Arguments.of(new UserLoginDTO(EMAIL, PASSWORD_WRONG_PATTERN), 400,
                        "Password muss contain exactly 10 characters and can contain only letters and digits"),
                Arguments.of(new UserLoginDTO(EMAIL, PASSWORD_1_CHAR_MORE), 400,
                        "Password muss contain exactly 10 characters and can contain only letters and digits"),
                Arguments.of(new UserLoginDTO(EMAIL, null), 400,
                        "password is mandatory")
        );
    }

    @BeforeEach
    void setUp() {
        User userFromDataBase = new User();
        when(userRepository.findByEmail(EMAIL)).thenReturn(
                Optional.of(User.builder()
                        .email(EMAIL)
                        .password(hashPassword(PASSWORD))
                        .build()));
    }

    @Test
    @DisplayName("Should return status 200 - OK and json web token")
    void authenticateUser_validCredentials_shouldReturnJwt() throws Exception {
        // given
        UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);

        // when and then
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(userLoginDTO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        final AuthAccessTokenDTO authAccessTokenDTO = objectMapper.readValue(content, AuthAccessTokenDTO.class);

        System.out.println("JWT Secret: " + secret);

        byte[] decodedKey = Base64.getDecoder().decode(secret);
        SecretKey secretKey = Keys.hmacShaKeyFor(decodedKey);


        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(authAccessTokenDTO.accessToken()).getPayload();

        Assertions.assertThat(claims.getSubject()).isNotBlank();
        Assertions.assertThat(EMAIL).isEqualTo(claims.getSubject());
    }


    @Test
    @DisplayName("Should return status 401 - Unauthorized and correct error message when user is not signed up")
    void authenticateUser_userNotRegistered_shouldReturnUnauthorized() throws Exception {
        // given
        UserLoginDTO loginDTO = new UserLoginDTO(NOT_EXISTING_EMAIL, PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginDTO))));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User with this email not exists"));

    }

    @Test
    @DisplayName("Should return status 400 - BadRequest for invalid email pattern")
    void authenticateUser_invalidEmailPattern_shouldReturnBadRequest() throws Exception {
        // given
        UserLoginDTO loginDTO = new UserLoginDTO(EMAIL_WRONG_PATTERN, PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginDTO))));

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return status 400 - BadRequest when email is null")
    void authenticateUser_emailIsNull_shouldReturnBadRequest() throws Exception {
        // given
        UserLoginDTO loginDTO = new UserLoginDTO(null, PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginDTO))));

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPasswords")
    @DisplayName("Should return status 400 - BadRequest and correct error message for invalid password pattern")
    void authenticateUser_invalidPasswordPattern_returnsBadRequestAndErrorMessage(
            UserLoginDTO loginDTO, Integer expectedStatus, String expectedMessage) throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginDTO))));

        // then
        resultActions
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.password").value(expectedMessage));


    }

    @Test
    @DisplayName("Should return status 401 - Unauthorized and correct error message")
    void authenticateUser_incorrectPassword_returnsBadRequestAndErrorMessage() throws Exception {
        // given
        UserLoginDTO loginDTO = new UserLoginDTO(EMAIL, WRONG_PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginDTO))));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User with this email or password not exists"));


    }


    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }


}