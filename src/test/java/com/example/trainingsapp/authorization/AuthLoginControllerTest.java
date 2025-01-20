package com.example.trainingsapp.authorization;

import com.example.trainingsapp.authorization.api.AuthService;
import com.example.trainingsapp.authorization.api.MyUserDetailsService;
import com.example.trainingsapp.authorization.api.dto.UserLoginDTO;
import com.example.trainingsapp.authorization.impl.AuthServiceImpl;
import com.example.trainingsapp.authorization.webtoken.JwtService;
import com.example.trainingsapp.general.exception.ErrorStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthServiceImpl.class),
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class, JwtService.class}))
class AuthLoginControllerTest {

    private static final String EMAIL = "example@email.com";
    private static final String PASSWORD = "String10ch";

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return status 200 - OK and json web token")
    void loginUser_validCredentials_shouldReturnJwt() throws Exception {
        // given
        UserLoginDTO userLoginDTO = new UserLoginDTO(EMAIL, PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));


    }


}