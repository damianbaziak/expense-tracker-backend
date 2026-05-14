package com.example.expensestracker.authorization.impl;

import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.api.dto.UserLoginDTO;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplLoginTest {
    private static final String encodedPassword = "encodedPassword";
    private static final String password = "correctPassword";
    private static final String wrongPassword = "wrongPassword";
    private static final String userEmail = "user@example.com";
    private static final String token = "mockedToken";

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Should return jason web token when credentials are correct")
    void loginUser_credentialsAreCorrect_shouldReturnJasonWebToken() {
        // given
        UserLoginDTO userLoginDTO = new UserLoginDTO(userEmail, password);
        User userFromDb = User.builder().email(userEmail).password(encodedPassword).build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), userFromDb.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(
                org.springframework.security.core.userdetails.User.builder().username(userEmail)
                        .password(encodedPassword).build());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);

        // when
        String result = authService.loginUser(userLoginDTO);

        // then
        Assertions.assertEquals(token, result);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));


    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when user not found")
    void loginUser_userNotFound_shouldThrowAnException() {
        // given
        UserLoginDTO userLoginDTO = new UserLoginDTO(userEmail, password);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class, () -> authService.loginUser(
                userLoginDTO
        ));

        Assertions.assertEquals("User with this email not exists", result.getDescription());
        Assertions.assertEquals(ErrorCode.U002.getBusinessCode(), result.getStatus());
        Assertions.assertEquals(ErrorCode.U002.getHttpStatus(), result.getHttpStatusCode());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(passwordEncoder, never()).matches(password, encodedPassword);
        verify(authenticationManager, never()).authenticate(any(Authentication.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));


    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when password does not match")
    void loginUser_passwordNotMatch_shouldThrowAnException() {
        // given
        UserLoginDTO userLoginDTO = new UserLoginDTO(userEmail, wrongPassword);
        User userFromDb = User.builder().email(userEmail).password(encodedPassword).build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

        // when and then
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class, () -> authService.loginUser(
                userLoginDTO
        ));

        Assertions.assertEquals("User with this email or password not exists", result.getDescription());
        Assertions.assertEquals(ErrorCode.U002.getBusinessCode(), result.getStatus());
        Assertions.assertEquals(ErrorCode.U002.getHttpStatus(), result.getHttpStatusCode());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(passwordEncoder, times(1)).matches(wrongPassword, encodedPassword);
        verify(authenticationManager, never()).authenticate(any(Authentication.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));


    }
}