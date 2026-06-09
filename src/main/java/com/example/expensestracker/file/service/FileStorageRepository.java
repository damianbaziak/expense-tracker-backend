package com.example.expensestracker.file.service;

import com.example.expensestracker.file.service.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<FileData, Long> {

}
