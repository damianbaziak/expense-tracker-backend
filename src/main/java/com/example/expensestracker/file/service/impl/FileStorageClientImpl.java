package com.example.expensestracker.file.service.impl;

import com.example.expensestracker.file.service.FileStorageClient;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageClientImpl implements FileStorageClient {
    private static final String FOLDER_PATH = "/Users/admin/Documents/files for expense-tracker/";

    @Override
    public byte[] readFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new AppRuntimeException(ErrorCode.FD002, "Filed to read file from file system", e);
        }
    }

    @Override
    public String writeFile(MultipartFile file) {
        String filePath = FOLDER_PATH + file.getOriginalFilename();
        try {
            file.transferTo(new File(filePath));
            return filePath;
        } catch (IOException e) {
            throw new AppRuntimeException(ErrorCode.FD003, "Cannot save file to file system", e);
        }
    }
}
