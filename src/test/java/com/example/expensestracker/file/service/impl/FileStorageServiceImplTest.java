package com.example.expensestracker.file.service.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.FileDataModelMapper;
import com.example.expensestracker.file.service.FileStorageRepository;
import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileStorageServiceImplTest {
    private static final String FOLDER_PATH = "/Users/admin/IdeaProjects/expenses-tracker/files";
    private static final Long TRANSACTION_ID = 1L;
    private static final Long FILE_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final FinancialTransaction TRANSACTION = TestUtils.createTransactionForTest(TRANSACTION_ID, EXPENSE);

    @Mock
    private FinancialTransactionRepository transactionRepository;
    @Mock
    private FileStorageRepository storageRepository;
    @Mock
    private FileDataModelMapper modelMapper;
    @InjectMocks
    private FileStorageServiceImpl storageService;

    @Test
    void testUploadFileForTransaction_validData() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "testFile.txt", "text/plain", "test".getBytes());
        FileData fileData = new FileData(
                FILE_ID, file.getName(), file.getContentType(), FOLDER_PATH + file.getOriginalFilename(), TRANSACTION);
        FileDataDTO expectedDTO = new FileDataDTO(fileData.getName(), fileData.getType(), TRANSACTION_ID);

        when(transactionRepository.findByIdAndWalletUserId(TRANSACTION_ID, USER_ID)).thenReturn(Optional.of(TRANSACTION));
        when(storageRepository.save(any(FileData.class))).thenReturn(fileData);
        when(modelMapper.mapFileDataEntityToFileDataDTO(fileData)).thenReturn(expectedDTO);

        // when
        FileDataDTO result = storageService.uploadFileForTransaction(file, TRANSACTION_ID, USER_ID);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(file.getName(), result.getName()),
                () -> Assertions.assertEquals(file.getContentType(), result.getType()),
                () -> Assertions.assertEquals(fileData.getFinancialTransaction().getId(), result.getTransactionId())
        );
        verify(modelMapper, times(1)).mapFileDataEntityToFileDataDTO(any(FileData.class));
        verify(storageRepository, times(1)).save(argThat(arg ->
                arg.getName().equals(fileData.getName()) && arg.getType().equals(fileData.getType())
                && arg.getFilePath().equals(fileData.getFilePath()) && arg.getFinancialTransaction().equals(TRANSACTION)));


    }
}