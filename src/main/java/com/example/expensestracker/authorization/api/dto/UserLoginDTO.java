package com.example.expensestracker.authorization.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class UserLoginDTO {


    @NotNull
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^[a-zA-Z0-9.]{10}$",
            message = "Password muss contain exactly 10 characters and can contain only letters and digits")
    @NotBlank(message = "password is mandatory")
    private String password;

    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginDTO() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLoginDTO that = (UserLoginDTO) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
