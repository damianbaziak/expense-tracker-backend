package com.example.expensestracker.financialtransaktioncategory.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class FinancialTransactionCategoryCreateDTO {
    @NotBlank
    @Size(max = 30, message = "Name size too long")
    @Pattern(regexp = "^[\\w\\s]+$")
    private String name;
    @NotNull
    private FinancialTransactionType type;

    public FinancialTransactionCategoryCreateDTO() {
    }

    public FinancialTransactionCategoryCreateDTO(String name, FinancialTransactionType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FinancialTransactionType getType() {
        return type;
    }

    public void setType(FinancialTransactionType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialTransactionCategoryCreateDTO that = (FinancialTransactionCategoryCreateDTO) o;
        return Objects.equals(name, that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "FinancialTransactionCategoryCreateDTO{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}


