package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.authorization.api.dto.UserLoginDTO;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class AuthLoginControllerIT extends IntegrationTest {

    private static final String LOGIN_USER_URL = "/api/auth/login";

    private static final UserLoginDTO USER_LOGIN_DTO = new UserLoginDTO("test@email.com", "1234567890");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }


    @Test
    public void authenticateUser_validCredentials_shouldReturnStatusOkAndJWT() throws Exception {

        User user = User.builder()
                .email("test@email.com")
                .password(hashPassword("1234567890"))
                .build();
        userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_USER_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_LOGIN_DTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty());

    }

    @Test
    public void authenticateUser_userNotExists_shouldReturnStatusUnauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_USER_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_LOGIN_DTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(
                        "User with this email not exists"));
    }



    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
