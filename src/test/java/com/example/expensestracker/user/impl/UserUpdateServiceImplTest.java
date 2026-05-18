package com.example.expensestracker.user.impl;

import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.dto.UserEmailUpdateDTO;
import com.example.expensestracker.user.api.dto.UserPasswordUpdateDTO;
import com.example.expensestracker.user.api.dto.UserUsernameUpdateDTO;
import com.example.expensestracker.user.api.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUpdateServiceImplTest {
    private static final Long USER_ID_1L = 1L;
    private static final Long PRINCIPAL_ID_1L = 1L;
    private static final int USER_AGE = 30;
    private static final String USER_FIRSTNAME = "Firstname_Example";
    private static final String USER_LASTNAME = "Lastname_Example";
    private static final String USER_EMAIL = "example@email.com";
    private static final String USER_PASSWORD = "52.345hg23";
    private static final String USER_USERNAME = "Username_Example";
    private static final String NEW_USERNAME = "New_Username";
    private static final String NEW_PASSWORD = "New_Password";
    private static final String NEW_EMAIL = "new_email@email.com";

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;


    @Test
    @DisplayName("Should return UserDTO with updated username when user exists")
    void updateUsername_whenUserExists_returnUserDTO() {
        // given
        UserUsernameUpdateDTO updateDTO = new UserUsernameUpdateDTO(NEW_USERNAME);
        User existingUser = User.builder()
                .id(USER_ID_1L).firstname(USER_FIRSTNAME).lastname(USER_LASTNAME).age(USER_AGE).email(USER_EMAIL)
                .username(USER_USERNAME).password(USER_PASSWORD).build();

        UserDTO expectedUserDTO = new UserDTO(
                USER_FIRSTNAME, USER_LASTNAME, USER_AGE, USER_EMAIL, NEW_USERNAME, USER_PASSWORD);

        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.of(existingUser));

        // when
        UserDTO result = userService.updateUsername(USER_ID_1L, updateDTO, PRINCIPAL_ID_1L);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedUserDTO);
        verify(userRepository).save(existingUser);

    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't exist")
    void updateUsername_whenUserNotExist_returnException() {
        // given
        UserUsernameUpdateDTO updateDTO = new UserUsernameUpdateDTO(NEW_PASSWORD);
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.updateUsername(USER_ID_1L, updateDTO, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getErrorBusinessCode(), ErrorCode.U003.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U003.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U003.getHttpStatus())
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't have permissions")
    void updateUsername_whenUserHasNoPermissions_returnException() {
        // given
        UserUsernameUpdateDTO updateDTO = new UserUsernameUpdateDTO(NEW_PASSWORD);
        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.updateUsername(2L, updateDTO, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getErrorBusinessCode(), ErrorCode.U004.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U004.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U004.getHttpStatus())
        );
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("Should return UserDTO with updated password when user exists")
    void updatePassword_whenUserExists_returnUserDTO() {
        // given
        UserPasswordUpdateDTO updateDTO = new UserPasswordUpdateDTO(NEW_PASSWORD);
        User existingUser = User.builder()
                .id(USER_ID_1L).firstname(USER_FIRSTNAME).lastname(USER_LASTNAME).age(USER_AGE).email(USER_EMAIL)
                .username(USER_USERNAME).password(USER_PASSWORD).build();

        UserDTO expectedUserDTO = new UserDTO(
                USER_FIRSTNAME, USER_LASTNAME, USER_AGE, USER_EMAIL, USER_USERNAME, NEW_PASSWORD);

        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.of(existingUser));

        // when
        UserDTO result = userService.updatePassword(USER_ID_1L, updateDTO, PRINCIPAL_ID_1L);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedUserDTO);
        verify(userRepository).save(existingUser);

    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't exist")
    void updatePassword_whenUserNotExist_returnException() {
        // given
        UserPasswordUpdateDTO updateDTO = new UserPasswordUpdateDTO(NEW_PASSWORD);
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.updatePassword(USER_ID_1L, updateDTO, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getErrorBusinessCode(), ErrorCode.U003.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U003.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U003.getHttpStatus())
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't have permissions")
    void updatePassword_whenUserHasNoPermissions_returnException() {
        // given
        UserPasswordUpdateDTO updateDTO = new UserPasswordUpdateDTO(NEW_PASSWORD);
        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.updatePassword(2L, updateDTO, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getErrorBusinessCode(), ErrorCode.U004.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U004.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U004.getHttpStatus())
        );
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("Should return UserDTO with updated email when user exists")
    void updateEmail_whenUserExists_returnUserDTO() {
        // given
        UserEmailUpdateDTO updateDTO = new UserEmailUpdateDTO(NEW_EMAIL);
        User existingUser = User.builder()
                .id(USER_ID_1L).firstname(USER_FIRSTNAME).lastname(USER_LASTNAME).age(USER_AGE).email(USER_EMAIL)
                .username(USER_USERNAME).password(USER_PASSWORD).build();

        UserDTO expectedUserDTO = new UserDTO(
                USER_FIRSTNAME, USER_LASTNAME, USER_AGE, NEW_EMAIL, USER_USERNAME, USER_PASSWORD);

        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.of(existingUser));

        // when
        UserDTO result = userService.updateEmail(USER_ID_1L, updateDTO, PRINCIPAL_ID_1L);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).usingRecursiveAssertion().isEqualTo(expectedUserDTO);
        verify(userRepository).save(existingUser);

    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't exist")
    void updateEmail_whenUserNotExist_returnException() {
        // given
        UserEmailUpdateDTO updateDTO = new UserEmailUpdateDTO(NEW_PASSWORD);
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.updateEmail(USER_ID_1L, updateDTO, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getErrorBusinessCode(), ErrorCode.U003.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U003.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U003.getHttpStatus())
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't have permissions")
    void updateEmail_whenUserHasNoPermissions_returnException() {
        // given
        UserEmailUpdateDTO updateDTO = new UserEmailUpdateDTO(NEW_PASSWORD);
        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.updateEmail(2L, updateDTO, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getErrorBusinessCode(), ErrorCode.U004.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U004.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U004.getHttpStatus())
        );
        verify(userRepository, never()).save(any(User.class));
    }


}



