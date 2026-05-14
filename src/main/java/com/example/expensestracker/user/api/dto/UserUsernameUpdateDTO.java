package com.example.expensestracker.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserUsernameUpdateDTO {

    @NotBlank(message = "username is mandatory")
    @Size(min = 2, max = 16, message = "username must be between 2 and 16 characters")
    private String username;

    public UserUsernameUpdateDTO(String username) {
        this.username = username;
    }

    public UserUsernameUpdateDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserUsernameUpdateDTO that = (UserUsernameUpdateDTO) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UsernameUpdateDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}

