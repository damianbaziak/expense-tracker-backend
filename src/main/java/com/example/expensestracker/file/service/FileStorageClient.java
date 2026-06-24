package com.example.expensestracker.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageClient {
    byte[] readFile(String filePath) throws IOException;

    String writeFile(MultipartFile file) throws IOException;
}
