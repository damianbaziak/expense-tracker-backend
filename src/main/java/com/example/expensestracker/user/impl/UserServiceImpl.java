package com.example.expensestracker.user.impl;

import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.dto.UserEmailUpdateDTO;
import com.example.expensestracker.user.api.dto.UserPasswordUpdateDTO;
import com.example.expensestracker.user.api.dto.UserUsernameUpdateDTO;
import com.example.expensestracker.user.api.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO findUserById(Long id, Long principalUserId) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.U003, "User does not exist"));

        if (!principalUserId.equals(id)) {
            throw new AppRuntimeException(ErrorCode.U004, "You can only access you own data");
        }

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUsername(Long id, UserUsernameUpdateDTO updateDTO, Long principalUserId) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.U003, "User does not exist"));

        if (!principalUserId.equals(id)) {
            throw new AppRuntimeException(ErrorCode.U004, "You can only access you own data");
        }

        if (updateDTO.getUsername() != null) {
            user.setUsername(updateDTO.getUsername());
        }
        userRepository.save(user);

        return new UserDTO(user.getFirstname(), user.getLastname(), user.getAge(),
                user.getEmail(), user.getUsername(), user.getPassword());

    }

    @Override
    @Transactional
    public UserDTO updatePassword(Long id, UserPasswordUpdateDTO updateDTO, Long loggedInUserId) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.U003, "User does not exist"));

        if (!loggedInUserId.equals(id)) {
            throw new AppRuntimeException(ErrorCode.U004, "You can only access you own data");
        }

        if (updateDTO.getPassword() != null) {
            user.setPassword(updateDTO.getPassword());
        }
        userRepository.save(user);

        return new UserDTO(user.getFirstname(), user.getLastname(), user.getAge(),
                user.getEmail(), user.getUsername(), user.getPassword());

    }

    @Override
    @Transactional
    public UserDTO updateEmail(Long id, UserEmailUpdateDTO updateDTO, Long principalUserId) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.U003, "User does not exist"));

        if (!principalUserId.equals(id)) {
            throw new AppRuntimeException(ErrorCode.U004, "You can only access you own data");
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        userRepository.save(user);

        return new UserDTO(user.getFirstname(), user.getLastname(), user.getAge(),
                user.getEmail(), user.getUsername(), user.getPassword());

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppRuntimeException(ErrorCode.U003,
                String.format("User with this email: %s doesn't exist", email)));
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getFirstname(), user.getLastname(), user.getAge(),
                user.getEmail(), user.getUsername(), user.getPassword());
    }
}




