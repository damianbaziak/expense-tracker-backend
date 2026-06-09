package com.example.expensestracker.file.service;

import com.example.expensestracker.file.api.dto.FileDataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    FileDataDTO uploadFileForTransaction(MultipartFile file, Long FinancialTransactionId, Long userId) throws IOException;
}
