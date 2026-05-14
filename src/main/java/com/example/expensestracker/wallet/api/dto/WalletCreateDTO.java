package com.example.expensestracker.wallet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class WalletCreateDTO {
    @NotBlank(message = "Name can not be blank")
    @Size(min = 2, max = 20, message =
            "Name should be between 2 and 20 characters long")
    @Pattern(regexp = "[\\w ]+", message = "Name can only contain letters, numbers, and spaces.")
    private String name;

    public WalletCreateDTO() {
    }

    public WalletCreateDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletCreateDTO that = (WalletCreateDTO) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "WalletCreateDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
