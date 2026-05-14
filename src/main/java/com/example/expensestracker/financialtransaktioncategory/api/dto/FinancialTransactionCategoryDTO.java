package com.example.expensestracker.financialtransaktioncategory.api.dto;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;

import java.time.Instant;
import java.util.Objects;

public class FinancialTransactionCategoryDTO {
    private Long id;
    private String name;
    private FinancialTransactionType type;
    private Instant creationDate;
    private Long userId;

    public FinancialTransactionCategoryDTO() {
    }

    public FinancialTransactionCategoryDTO(Long id, String name, FinancialTransactionType type, Instant creationDate, Long userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialTransactionCategoryDTO that = (FinancialTransactionCategoryDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && type == that.type && Objects.equals(creationDate, that.creationDate) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, creationDate, userId);
    }

    @Override
    public String toString() {
        return "FinancialTransactionCategoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", creationDate=" + creationDate +
                ", userId=" + userId +
                '}';
    }
}
