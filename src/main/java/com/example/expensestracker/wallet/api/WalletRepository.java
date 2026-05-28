package com.example.expensestracker.wallet.api;

import com.example.expensestracker.wallet.api.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByName(String name);

    List<Wallet> findAllByUserIdOrderByNameAsc(Long userId);

    Optional<Wallet> findByIdAndUserId(Long walletId, Long userId);

    List<Wallet> findAllByUserIdAndNameIsContainingIgnoreCase(Long userId, String name);
}
