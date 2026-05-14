package com.example.expensestracker.authorization.api;

import com.example.expensestracker.authorization.api.dto.UserLoginDTO;
import com.example.expensestracker.user.api.dto.UserDTO;

public interface AuthService {

    void registerUser(UserDTO userDTO);

    String loginUser(UserLoginDTO userLoginDTO);


}
