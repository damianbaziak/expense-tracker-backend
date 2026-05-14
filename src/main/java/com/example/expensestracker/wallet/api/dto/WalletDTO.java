package com.example.expensestracker.wallet.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class WalletDTO {
    private Long id;
    private String name;
    private Instant creationDate;
    private Long userId;
    private BigDecimal balance;

    public WalletDTO() {
    }

    public WalletDTO(Long id, String name, Instant creationDate, Long userId, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.userId = userId;
        this.balance = balance;
    }

    public WalletDTO(Long id, String name, Instant creationDate, Long userId) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletDTO walletDTO = (WalletDTO) o;
        return Objects.equals(id, walletDTO.id) && Objects.equals(name, walletDTO.name) && Objects.equals(creationDate, walletDTO.creationDate) && Objects.equals(userId, walletDTO.userId) && Objects.equals(balance, walletDTO.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creationDate, userId, balance);
    }

    @Override
    public String toString() {
        return "WalletDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
