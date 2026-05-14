package com.example.expensestracker.user.api.model;

import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.wallet.api.model.Wallet;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private int age;
    private String email;
    private String username;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FinancialTransactionCategory> financialTransactionCategories;

    public User() {
    }

    public User(Long id, String firstname, String lastname, int age, String email, String username, String password,
                List<Wallet> wallets, List<FinancialTransactionCategory> financialTransactionCategories) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.email = email;
        this.username = username;
        this.password = password;
        this.wallets = wallets;
        this.financialTransactionCategories = financialTransactionCategories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    public List<FinancialTransactionCategory> getFinancialTransactionCategories() {
        return financialTransactionCategories;
    }

    public void setFinancialTransactionCategories(List<FinancialTransactionCategory> financialTransactionCategories) {
        this.financialTransactionCategories = financialTransactionCategories;
    }


}
