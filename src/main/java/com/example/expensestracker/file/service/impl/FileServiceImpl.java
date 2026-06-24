package com.example.expensestracker.file.service.impl;

import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.FileDataModelMapper;
import com.example.expensestracker.file.service.FileDataRepository;
import com.example.expensestracker.file.service.FileService;
import com.example.expensestracker.file.service.FileStorageClient;
import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDataRepository storageRepository;
    @Autowired
    private FinancialTransactionRepository transactionRepository;
    @Autowired
    private FileDataModelMapper modelMapper;
    @Autowired
    private FileStorageClient fileStorageClient;

    @Override
    public FileDataDTO uploadFileForTransaction(MultipartFile multipartFile, Long financialTransactionId,
                                                Long userId) throws IOException {
        FinancialTransaction financialTransaction = transactionRepository.findByIdAndWalletUserId(
                financialTransactionId, userId).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.FT001, "Financial transaction with id "
                        + financialTransactionId + " not found"));

        String storedFilePath = fileStorageClient.writeFile(multipartFile);

        FileData fileData = FileData.builder()
                .fileName(multipartFile.getOriginalFilename())
                .type(multipartFile.getContentType())
                .filePath(storedFilePath)
                .financialTransaction(financialTransaction).build();
        FileData savedFileData = storageRepository.save(fileData);

        return modelMapper.mapFileDataEntityToFileDataDTO(savedFileData);
    }

    @Override
    public byte[] downloadFile(Long fileDataId, Long userId) throws IOException {
        FileData fileDataFromDB = storageRepository.findByIdAndFinancialTransactionWalletUserId(fileDataId, userId)
                .orElseThrow(() -> new AppRuntimeException(ErrorCode.FD001, String.format(
                        "FileData with this id: %d not exist", fileDataId)));
        String filePath = fileDataFromDB.getFilePath();

        return fileStorageClient.readFile(filePath);
    }
}
