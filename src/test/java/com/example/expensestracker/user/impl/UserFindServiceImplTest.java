package com.example.expensestracker.user.impl;

import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.dto.UserDTO;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFindServiceImplTest {
    private static final Long USER_ID_1L = 1L;
    private static final Long PRINCIPAL_ID_1L = 1L;
    private static final int USER_AGE = 30;
    private static final String USER_FIRSTNAME = "Firstname_Example";
    private static final String USER_LASTNAME = "Lastname_Example";
    private static final String USER_EMAIL = "example@email.com";
    private static final String USER_PASSWORD = "52345hg23jk4h5";
    private static final String USER_USERNAME = "Username_Example";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should return userDTO when user exists in Database")
    void findUserById_whenUserExists_returnUserDTO() {
        // given
        User user = User.builder()
                .id(USER_ID_1L).firstname(USER_FIRSTNAME).lastname(USER_LASTNAME).age(USER_AGE).email(USER_EMAIL)
                .username(USER_USERNAME).password(USER_PASSWORD).build();

        UserDTO userDTO = new UserDTO(
                USER_FIRSTNAME, USER_LASTNAME, USER_AGE, USER_EMAIL, USER_USERNAME, USER_PASSWORD);

        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.of(user));

        // when
        UserDTO result = userService.findUserById(USER_ID_1L, PRINCIPAL_ID_1L);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(userDTO);

    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't exist")
    void findUserById_whenUserNotExists_returnException() {
        // given
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.findUserById(USER_ID_1L, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getBusinessCode(), ErrorCode.U003.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U003.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U003.getHttpStatus())
        );
    }

    @Test
    @DisplayName("Should return an AppRuntimeException when user doesn't have permissions")
    void findUserById_whenUserHasNoPermissions_returnException() {
        // given
        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).build()));

        // when and then
        AppRuntimeException result = assertThrows(
                AppRuntimeException.class, () -> userService.findUserById(2L, PRINCIPAL_ID_1L));

        assertAll(
                () -> assertEquals(result.getBusinessCode(), ErrorCode.U004.getBusinessCode()),
                () -> assertEquals(result.getMessage(), ErrorCode.U004.getBusinessMessage()),
                () -> assertEquals(result.getHttpStatusCode(), ErrorCode.U004.getHttpStatus())
        );
    }
}