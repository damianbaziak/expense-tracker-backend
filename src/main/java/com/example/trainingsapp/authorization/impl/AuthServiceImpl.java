package com.example.trainingsapp.authorization.impl;

import com.example.trainingsapp.authorization.api.AuthService;
import com.example.trainingsapp.authorization.api.MyUserDetailsService;
import com.example.trainingsapp.authorization.api.dto.UserLoginDTO;
import com.example.trainingsapp.authorization.webtoken.JwtService;
import com.example.trainingsapp.general.exception.AppRuntimeException;
import com.example.trainingsapp.general.exception.ErrorCode;
import com.example.trainingsapp.user.api.UserRepository;
import com.example.trainingsapp.user.api.dto.UserDTO;
import com.example.trainingsapp.user.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public void registerUser(UserDTO userDTO) {
        Optional<User> userFromDb = userRepository.findByEmail(userDTO.getEmail());
        if (userFromDb.isPresent()) {
            throw new AppRuntimeException(ErrorCode.U001, "User with this email already exists");
        }

        User user = User.builder()
                .firstname(userDTO.getFirstname())
                .lastname(userDTO.getLastname())
                .age(userDTO.getAge())
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .password(hashedPassword(userDTO.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Override
    public String loginUser(UserLoginDTO userLoginDTO) {
        Optional<User> userFromDb = userRepository.findByEmail(userLoginDTO.getEmail());
        if (userFromDb.isEmpty()) {
            throw new AppRuntimeException(ErrorCode.U002, "User with this email not exists");
        }

        User user = userFromDb.get();
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new AppRuntimeException(ErrorCode.U002, "User with this email or password not exists");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginDTO.getEmail(), userLoginDTO.getPassword()));

        return jwtService.generateToken(userDetailsService.loadUserByUsername(userLoginDTO.getEmail()));

    }

    public String hashedPassword(String password) {
        return passwordEncoder.encode(password);
    }


}
