package com.example.expensestracker.file.service.impl;

import com.example.expensestracker.file.service.FileDataRepository;
import com.example.expensestracker.file.service.FileStorageClient;
import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplDownloadTest {
    private static final String FILE_PATH = "/Example/folder/path/document.pdf";
    private static final byte[] EXPECTED_CONTENT = "text_example".getBytes();
    private static final Long FILE_DATA_ID = 1L;
    private static final Long USER_ID = 1L;
    @Mock
    private FileDataRepository fileDataRepository;
    @Mock
    private FileStorageClient fileStorageClient;
    @InjectMocks
    private FileServiceImpl storageService;

    @Test
    void shouldReturnFileContent() throws IOException {
        FileData fileData = new FileData(FILE_DATA_ID, null, MediaType.APPLICATION_PDF_VALUE,
                FILE_PATH, null);
        when(fileDataRepository.findByIdAndFinancialTransactionWalletUserId(FILE_DATA_ID, USER_ID)).thenReturn(
                Optional.of(fileData));
        when(fileStorageClient.readFile(anyString())).thenReturn(EXPECTED_CONTENT);

        // when
        byte[] result = storageService.downloadFile(FILE_DATA_ID, USER_ID);

        // then
        Assertions.assertArrayEquals(EXPECTED_CONTENT, result);
        verify(fileStorageClient, times(1)).readFile(FILE_PATH);
    }

    @Test
    void shouldThrowExceptionWhenFileDataNotFound() throws IOException {
        FileData fileData = new FileData(FILE_DATA_ID, null, MediaType.APPLICATION_PDF_VALUE,
                FILE_PATH, null);
        when(fileDataRepository.findByIdAndFinancialTransactionWalletUserId(FILE_DATA_ID, USER_ID)).thenReturn(
                Optional.empty());
        // when
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class,
                () -> storageService.downloadFile(FILE_DATA_ID, USER_ID));

        // then
        Assertions.assertEquals(ErrorCode.FD001, result.getErrorCode());
        verify(fileStorageClient, times(0)).readFile(FILE_PATH);
    }
}
