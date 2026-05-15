package com.example.expensestracker.integration;

import com.example.expensestracker.IntegrationTest;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.model.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class AuthRegisterControllerIT extends IntegrationTest {

    private static final String REGISTER_USER_URL = "/api/auth/register";

    private static final UserDTO USER_DTO = new UserDTO(
            "test_firstname", "test_lastname", 50, "test@email.com",
            "test_username", "1234567890");

    private static final UserDTO USER_DTO_TO_SHORT_PASSWORD = new UserDTO(
            "test_firstname", "test_lastname", 50, "test@email.com",
            "test_username", "123");

    private static final UserDTO USER_DTO_INVALID_EMAIL = new UserDTO(
            "test_firstname", "test_lastname", 50, "invalid.email",
            "test_username", "1234567890");

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_withValidUser_shouldReturnStatusCreated() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_USER_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_DTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        User user = userRepository.findByEmail(USER_DTO.getEmail()).orElse(null);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(USER_DTO.getEmail(), user.getEmail());
        Assertions.assertEquals(USER_DTO.getUsername(), user.getUsername());
        Assertions.assertEquals(USER_DTO.getLastname(), user.getLastname());

    }

    @Test
    void registerUser_userAlreadyExists_shouldReturnStatusBadRequestAndCorrectMessage() throws Exception {
        final User existingUser = User.builder()
                .firstname(USER_DTO.getFirstname())
                .lastname(USER_DTO.getLastname())
                .email(USER_DTO.getEmail())
                .age(USER_DTO.getAge())
                .password(USER_DTO.getPassword())
                .username(USER_DTO.getUsername()).build();

        userRepository.save(existingUser);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_USER_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(existingUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("User with this email already exists"));
    }

    @Test
    void registerUser_toShortPassword_shouldReturnStatusBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_USER_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_DTO_TO_SHORT_PASSWORD)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.descriptionList[0]").value(
                        Matchers.containsString("Password muss contain exactly 10 characters and can contain " +
                                "only letters and digits"
                        )));
    }

    @Test
    void registerUser_invalidEmail_shouldReturnStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_USER_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_DTO_INVALID_EMAIL)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.descriptionList[0]").value(
                        Matchers.containsString("invalid email address")));
    }
}
