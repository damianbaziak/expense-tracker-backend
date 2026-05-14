package com.example.expensestracker.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class UserEmailUpdateDTO {
    @Email
    @NotNull
    private String email;

    public UserEmailUpdateDTO(String email) {
        this.email = email;
    }

    public UserEmailUpdateDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEmailUpdateDTO that = (UserEmailUpdateDTO) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "EmailUptadeDTO{" +
                "email='" + email + '\'' +
                '}';
    }
}
