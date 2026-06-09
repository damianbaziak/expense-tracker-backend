package com.example.expensestracker.file.service.impl;

import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.FileDataModelMapper;
import com.example.expensestracker.file.service.FileStorageRepository;
import com.example.expensestracker.file.service.FileStorageService;
import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final String FOLDER_PATH = "/Users/admin/IdeaProjects/expenses-tracker/files";
    @Autowired
    private FileStorageRepository storageRepository;
    @Autowired
    private FinancialTransactionRepository transactionRepository;
    @Autowired
    private FileDataModelMapper modelMapper;

    @Override
    public FileDataDTO uploadFileForTransaction(MultipartFile file, Long financialTransactionId, Long userId) throws IOException {
        FinancialTransaction financialTransaction = transactionRepository.findByIdAndWalletUserId(
                financialTransactionId, userId).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.FT001, "Financial transaction with id "
                        + financialTransactionId + " not found"));

        String filePath = FOLDER_PATH + file.getOriginalFilename();

        FileData fileData = FileData.builder()
                .name(file.getName())
                .type(file.getContentType())
                .filePath(filePath)
                .financialTransaction(financialTransaction).build();
        FileData savedFileData = storageRepository.save(fileData);

        file.transferTo(new File(filePath));
        return modelMapper.mapFileDataEntityToFileDataDTO(savedFileData);
    }
}
