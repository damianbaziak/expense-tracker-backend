package com.example.expensestracker.wallet.api.model;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.user.api.model.User;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "wallets")

public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Column(name = "creation_date")
    private Instant creationDate;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancialTransaction> financialTransactionList = new ArrayList<>();

    public Wallet() {
    }

    public Wallet(String name, User user) {
        this.name = name;
        this.user = user;
        this.creationDate = Instant.now();
    }

    public Wallet(Long id, User user, String name, Instant creationDate, List<FinancialTransaction>
            financialTransactionList) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.creationDate = creationDate;
        this.financialTransactionList = financialTransactionList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public List<FinancialTransaction> getFinancialTransactionList() {
        return financialTransactionList;
    }

    public void setFinancialTransactionList(List<FinancialTransaction> financialTransactionList) {
        this.financialTransactionList = financialTransactionList;
    }
}
