package com.example.expensestracker.user;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.authorization.JwtAuthorizationFilter;
import com.example.expensestracker.authorization.WebSecurityConfiguration;
import com.example.expensestracker.authorization.api.MyUserDetailsService;
import com.example.expensestracker.authorization.webtoken.JwtService;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.general.exception.ErrorStrategy;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.user.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ErrorStrategy.class, WebSecurityConfiguration.class, JwtService.class, MyUserDetailsService.class,
                        JwtAuthorizationFilter.class}),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {UserServiceImpl.class}))
class UserGetControllerTest {

    private static final Long ID_1L = 1L;
    private static final String USER_EMAIL = "example@email.com";
    private static final int USER_AGE = 30;
    private static final String USER_FIRSTNAME = "Firstname_Example";
    private static final String USER_LASTNAME = "Lastname_Example";
    private static final String USER_PASSWORD = "52345hg23jk4h5";
    private static final String USER_USERNAME = "Username_Example";
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status OK and userDTO when user exists")
    void getUserById_whenUserExists_shouldReturnUserDTO() throws Exception {
        // given
        User userPrincipal = TestUtils.createUserForTest();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(userPrincipal);

        UserDTO userDTO = new UserDTO(
                USER_FIRSTNAME, USER_LASTNAME, USER_AGE, USER_EMAIL, USER_USERNAME, USER_PASSWORD);

        when(userService.findUserById(ID_1L, userPrincipal.getId())).thenReturn(userDTO);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/{id}", ID_1L));

        // then
        result.andExpect(status().isOk());
        result.andExpect(content().string(new ObjectMapper().writeValueAsString(userDTO)));

    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status Not Found when user doesn't exist")
    void getUserById_whenUserNotExist_shouldReturnStatusNotFound() throws Exception {
        // given
        User userPrincipal = TestUtils.createUserForTest();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(userPrincipal);

        doThrow(new AppRuntimeException(ErrorCode.U003, "User does not exist"))
                .when(userService).findUserById(ID_1L, userPrincipal.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/users/{id}", ID_1L));

        // then
        result.andDo(MockMvcResultHandlers.print());
        result.andExpectAll(
                MockMvcResultMatchers.status().isNotFound(),
                MockMvcResultMatchers.jsonPath("$.description").value("User does not exist"),
                MockMvcResultMatchers.jsonPath("$.businessCode").value(ErrorCode.U003.getBusinessCode()));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    @DisplayName("Should return HTTP status Forbidden when user doesn't have permissions")
    void getUserById_whenUserHasNoPermissions_shouldReturnStatusForbidden() throws Exception {
        // given
        User userPrincipal = TestUtils.createUserForTest();
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(userPrincipal);

        doThrow(new AppRuntimeException(ErrorCode.U004, "You can only access you own data"))
                .when(userService).findUserById(ID_1L, userPrincipal.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/users/{id}", ID_1L));

        // then
        result.andExpect(status().isForbidden());
        result.andExpect(content().string(containsString("You can only access you own data")));
    }


}
