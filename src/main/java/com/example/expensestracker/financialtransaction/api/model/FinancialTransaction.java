package com.example.expensestracker.financialtransaction.api.model;

import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.wallet.api.model.Wallet;
import jakarta.persistence.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Entity
@Table(name = "financial_transactions")
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    // This annotation is used to specify how an enum type should be stored in the database.
    // EnumType.STRING indicates that the enum value will be stored as a string in the database. For example, if the enum
    // value is INCOME, the corresponding value in the database will be the string "INCOME".
    @Enumerated(EnumType.STRING)
    // 'columnDefinition': This is used to define the column type in the database.
    // The columnDefinition specifies that the transaction_type column should be an ENUM type with possible values 'INCOME' or 'EXPENSE'.
    // Note that: columnDefinition is typically used to define the exact SQL fragment for the column, which can be useful for defining
    // database-specific column types like ENUM.
    @Column(name = "transaction_type", columnDefinition = "ENUM('INCOME', 'EXPENSE')")
    private FinancialTransactionType type;

    //@DecimalMin("0,0")
    private BigDecimal amount;

    @Column(name = "transaction_date")
    //@DateTimeFormat(pattern = "yyyy-mm-dd hh-mm-ss")
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    private FinancialTransactionCategory financialTransactionCategory;

    private String description;

    public FinancialTransaction() {
    }

    public FinancialTransaction(Long id, Wallet wallet, FinancialTransactionType type, BigDecimal amount, Instant date,
                                FinancialTransactionCategory financialTransactionCategory, String description) {
        this.id = id;
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.financialTransactionCategory = financialTransactionCategory;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public FinancialTransactionType getType() {
        return type;
    }

    public void setType(FinancialTransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public FinancialTransactionCategory getFinancialTransactionCategory() {
        return financialTransactionCategory;
    }

    public void setFinancialTransactionCategory(FinancialTransactionCategory financialTransactionCategory) {
        this.financialTransactionCategory = financialTransactionCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


