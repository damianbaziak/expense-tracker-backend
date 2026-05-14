package com.example.expensestracker.user.api;

import com.example.expensestracker.user.api.dto.UserEmailUpdateDTO;
import com.example.expensestracker.user.api.dto.UserPasswordUpdateDTO;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.dto.UserUsernameUpdateDTO;
import com.example.expensestracker.user.api.model.User;

public interface UserService {
    UserDTO findUserById(Long id, Long principalUserId);

    UserDTO updateUsername(Long id, UserUsernameUpdateDTO updateDTO, Long principalUserId);

    UserDTO updateEmail(Long id, UserEmailUpdateDTO updateDTO, Long principalUserId);

    UserDTO updatePassword(Long id, UserPasswordUpdateDTO updateDTO, Long principalUserId);

    User findUserByEmail(String email);


}
