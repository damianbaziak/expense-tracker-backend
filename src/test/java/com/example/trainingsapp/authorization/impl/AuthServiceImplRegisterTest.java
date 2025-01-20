package com.example.trainingsapp.authorization.impl;

import com.example.trainingsapp.general.exception.AppRuntimeException;
import com.example.trainingsapp.general.exception.ErrorCode;
import com.example.trainingsapp.user.api.UserRepository;
import com.example.trainingsapp.user.api.dto.UserDTO;
import com.example.trainingsapp.user.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplRegisterTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Test
    @DisplayName("Should create and save new user to the database")
    void addUser_newUser_shouldSaveUser() {
        // given
        UserDTO userDTO = new UserDTO("damian", "baziak", 30, "damianbaziak@gmail.com",
                "bazyl", "1234567890");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        authService.registerUser(userDTO);

        // then
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();


        assertEquals(userDTO.getFirstname(), capturedUser.getFirstname());
        assertEquals(userDTO.getLastname(), capturedUser.getLastname());
        assertEquals(userDTO.getAge(), capturedUser.getAge());
        assertEquals(userDTO.getEmail(), capturedUser.getEmail());
        assertEquals(userDTO.getUsername(), capturedUser.getUsername());
        assertNotEquals(userDTO.getPassword(), capturedUser.getPassword());
    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when user with the same email exists")
    void addUser_userWithEmailExists_shouldThrowException() {
        // given
        UserDTO userDTO = new UserDTO("damian", "baziak", 30, "damianbaziak@gmail.com",
                "bazyl", "1234567890");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(
                Optional.of(User.builder().email(userDTO.getEmail()).build()));

        // when
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> authService.registerUser(userDTO),
                "User with this email already exists");

        // then
        verify(userRepository, never()).save(any(User.class));
        assertEquals(result.getMessage(), ErrorCode.U001.getBusinessMessage());
    }

    @Test
    @DisplayName("Hashing a valid password should produce a result of correct length")
    void hashedPassword_validPassword_shouldReturnCorrectLengthHash() {
        // given
        String passsword = "String123.";

        // when
        String result = authService.hashedPassword(passsword);

        // then
        assertEquals(60, result.length());

    }
}