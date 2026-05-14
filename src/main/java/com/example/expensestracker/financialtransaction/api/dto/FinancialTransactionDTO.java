package com.example.expensestracker.financialtransaction.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class FinancialTransactionDTO {

    private Long id;

    private BigDecimal amount;

    // The @JsonInclude(JsonInclude.Include.ALWAYS) annotation in Java ensures that the description field is always
    // included when the object is serialized to JSON, regardless of whether its value is null, empty, or the default.
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String description;

    private FinancialTransactionType type;

    private Instant date;

    private Long categoryId;

    public FinancialTransactionDTO() {
    }

    public FinancialTransactionDTO(Long id, BigDecimal amount, String description, FinancialTransactionType type, Instant date, Long categoryId) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.date = date;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        FinancialTransactionDTO that = (FinancialTransactionDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(amount, that.amount) && Objects.equals(description, that.description) && type == that.type && Objects.equals(date, that.date) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, description, type, date, categoryId);
    }

    @Override
    public String toString() {
        return "FinancialTransactionDTO{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", categoryId=" + categoryId +
                '}';
    }
}