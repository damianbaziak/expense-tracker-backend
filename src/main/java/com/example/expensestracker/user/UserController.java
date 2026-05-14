package com.example.expensestracker.user;

import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.dto.UserDTO;
import com.example.expensestracker.user.api.dto.UserEmailUpdateDTO;
import com.example.expensestracker.user.api.dto.UserPasswordUpdateDTO;
import com.example.expensestracker.user.api.dto.UserUsernameUpdateDTO;
import com.example.expensestracker.user.api.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@Min(1) @NotNull @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User loggedInUser = userService.findUserByEmail(email);
        Long loggedInUserId = loggedInUser.getId();

        UserDTO user = userService.findUserById(id, loggedInUserId);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/username")
    public ResponseEntity<UserDTO> updateUsername(
            @Min(1) @NotNull @PathVariable Long id,
            @Valid @RequestBody UserUsernameUpdateDTO updateDTO, Principal principal) {
        String email = principal.getName();
        User principalUser = userService.findUserByEmail(email);
        Long principalUserId = principalUser.getId();

        UserDTO updatedUser = userService.updateUsername(id, updateDTO, principalUserId);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity updateEmail(
            @Min(1) @NotNull @PathVariable Long id,
            @Valid @RequestBody UserEmailUpdateDTO updateDTO, Principal principal) {
        String email = principal.getName();
        User principalUser = userService.findUserByEmail(email);
        Long principalUserId = principalUser.getId();

        UserDTO updatedUser = userService.updateEmail(id, updateDTO, principalUserId);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);

    }

    @PatchMapping("/{id}/password")
    public ResponseEntity updatePassword(
            @Min(1) @NotNull @PathVariable Long id,
            @Valid @RequestBody UserPasswordUpdateDTO updateDTO, Principal principal) {
        String email = principal.getName();
        User principalUser = userService.findUserByEmail(email);
        Long principalUserId = principalUser.getId();

        UserDTO updatedUser = userService.updatePassword(id, updateDTO, principalUserId);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);

    }

}