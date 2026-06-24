package com.example.expensestracker.file.service.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.FileDataModelMapper;
import com.example.expensestracker.file.service.FileDataRepository;
import com.example.expensestracker.file.service.FileStorageClient;
import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplUploadTest {
    private static final Long TRANSACTION_ID = 1L;
    private static final Long FILE_DATA_ID = 1L;
    private static final String FILE_PATH = "/Example/folder/path/document.pdf";
    private static final Long USER_ID = 1L;
    private static final FinancialTransaction TRANSACTION = TestUtils.createTransactionForTest(TRANSACTION_ID, EXPENSE);
    private static final MockMultipartFile MOCK_MULTIPART = new MockMultipartFile(
            "file", "document.pdf", MediaType.APPLICATION_PDF_VALUE, "text_example".getBytes());

    @Mock
    private FinancialTransactionRepository transactionRepository;
    @Mock
    private FileDataRepository fileDataRepository;
    @Mock
    private FileDataModelMapper modelMapper;
    @Mock
    private FileStorageClient fileStorageClient;
    @InjectMocks
    private FileServiceImpl storageService;

    @Test
    void shouldSaveFileAndReturnFileDataDTO() throws IOException {
        FileData fileData = new FileData(
                FILE_DATA_ID, MOCK_MULTIPART.getOriginalFilename(), MOCK_MULTIPART.getContentType(), null, TRANSACTION);
        FileDataDTO expectedDTO = new FileDataDTO(fileData.getFileName(), fileData.getType(), TRANSACTION_ID);

        when(transactionRepository.findByIdAndWalletUserId(TRANSACTION_ID, USER_ID)).thenReturn(Optional.of(TRANSACTION));
        when(fileStorageClient.writeFile(any(MockMultipartFile.class))).thenReturn(FILE_PATH);
        when(fileDataRepository.save(any(FileData.class))).thenReturn(fileData);
        when(modelMapper.mapFileDataEntityToFileDataDTO(any(FileData.class))).thenReturn(expectedDTO);

        // when
        FileDataDTO result = storageService.uploadFileForTransaction(MOCK_MULTIPART, TRANSACTION_ID, USER_ID);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(MOCK_MULTIPART.getOriginalFilename(), result.getFileName()),
                () -> Assertions.assertEquals(MOCK_MULTIPART.getContentType(), result.getType()),
                () -> Assertions.assertEquals(TRANSACTION_ID, result.getTransactionId())
        );
        verify(fileStorageClient, times(1)).writeFile(MOCK_MULTIPART);
        verify(modelMapper, times(1)).mapFileDataEntityToFileDataDTO(fileData);
        verify(fileDataRepository, times(1)).save(argThat(arg ->
                arg.getFilePath().equals(FILE_PATH)));
    }

    @Test
    void shouldThrowAppRuntimeExceptionWhenTransactionNotFound() {
        when(transactionRepository.findByIdAndWalletUserId(TRANSACTION_ID, USER_ID)).thenReturn(Optional.empty());

        // when
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class,
                () -> storageService.uploadFileForTransaction(MOCK_MULTIPART, TRANSACTION_ID, USER_ID));

        // then
        verify(modelMapper, times(0)).mapFileDataEntityToFileDataDTO(any(FileData.class));
        verify(fileDataRepository, times(0)).save(any(FileData.class));
        Assertions.assertEquals(ErrorCode.FT001.getHttpStatus(), result.getHttpStatusCode());
    }


}