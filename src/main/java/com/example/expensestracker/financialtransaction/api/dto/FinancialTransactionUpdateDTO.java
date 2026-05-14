package com.example.expensestracker.financialtransaction.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class FinancialTransactionUpdateDTO {

    @Digits(integer = 12, fraction = 2)
    @PositiveOrZero
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    @NotNull
    private FinancialTransactionType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant date;

    private Long categoryId;

    public FinancialTransactionUpdateDTO() {
    }

    public FinancialTransactionUpdateDTO(BigDecimal amount, String description, FinancialTransactionType type, Instant date, Long categoryId) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.date = date;
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FinancialTransactionType getType() {
        return type;
    }

    public void setType(FinancialTransactionType type) {
        this.type = type;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialTransactionUpdateDTO that = (FinancialTransactionUpdateDTO) o;
        return Objects.equals(amount, that.amount) && Objects.equals(description, that.description) && type == that.type && Objects.equals(date, that.date) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, description, type, date, categoryId);
    }

    @Override
    public String toString() {
        return "FinancialTransactionUpdateDTO{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", categoryId=" + categoryId +
                '}';
    }
}
