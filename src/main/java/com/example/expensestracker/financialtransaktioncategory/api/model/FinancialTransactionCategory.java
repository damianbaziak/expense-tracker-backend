package com.example.expensestracker.financialtransaktioncategory.api.model;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.user.api.model.User;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "financial_transactions_categories")
@Builder
public class FinancialTransactionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", columnDefinition = "ENUM('INCOME', 'EXPENSE')")
    private FinancialTransactionType type;

    @OneToMany(mappedBy = "financialTransactionCategory", fetch = FetchType.LAZY)
    private List<FinancialTransaction> financialTransactions;

    @Column(name = "creation_date")
    private Instant creationDate;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public FinancialTransactionCategory() {
    }

    public FinancialTransactionCategory(Long id, String name, FinancialTransactionType type, List<FinancialTransaction> financialTransactions, Instant creationDate, User user) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.financialTransactions = financialTransactions;
        this.creationDate = creationDate;
        this.user = user;
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

    public List<FinancialTransaction> getFinancialTransactions() {
        return financialTransactions;
    }

    public void setFinancialTransactions(List<FinancialTransaction> financialTransactions) {
        this.financialTransactions = financialTransactions;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        creationDate = Instant.now();
    }


}
