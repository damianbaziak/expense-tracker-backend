package com.example.expensestracker.file.service;

import com.example.expensestracker.file.api.dto.FileDataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    FileDataDTO uploadFileForTransaction(MultipartFile file, Long FinancialTransactionId, Long userId) throws IOException;

    byte[] downloadFile(Long fileId, Long userId) throws IOException;
}
