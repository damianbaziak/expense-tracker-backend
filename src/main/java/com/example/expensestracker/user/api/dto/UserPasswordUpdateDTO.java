package com.example.expensestracker.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class UserPasswordUpdateDTO {

    @Pattern(
            regexp = "^[a-zA-Z0-9.]{10}$",
            message = "Password muss  contain exactly 10 characters and can contain only letters and digits")
    @NotBlank(message = "Password can not be blank")
    private String password;

    public UserPasswordUpdateDTO(String password) {
        this.password = password;
    }

    public UserPasswordUpdateDTO() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPasswordUpdateDTO that = (UserPasswordUpdateDTO) o;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }

    @Override
    public String toString() {
        return "UserPasswordUpdateDTO{" +
                "password='" + password + '\'' +
                '}';
    }
}
