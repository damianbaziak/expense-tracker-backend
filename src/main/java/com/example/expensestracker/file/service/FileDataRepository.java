package com.example.expensestracker.file.service;

import com.example.expensestracker.file.service.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileDataRepository extends JpaRepository<FileData, Long> {
    Optional<FileData> findByIdAndFinancialTransactionWalletUserId(Long id, Long userId);

}
