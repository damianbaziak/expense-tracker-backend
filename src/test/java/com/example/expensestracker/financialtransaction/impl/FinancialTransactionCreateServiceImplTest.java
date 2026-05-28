package com.example.expensestracker.financialtransaction.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static java.math.BigDecimal.ONE;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Mockito Initialization:
// @ExtendWith(MockitoExtension.class) tells JUnit 5 to use Mockito's extension when running the test. This extension is responsible
// for initializing and configuring all fields annotated with Mockito annotations (such as @Mock, @InjectMocks, etc.) before each test method is executed.
// Automatic Mock Creation and Injection:
// The extension automatically creates mock instances for fields annotated with @Mock.
// It injects these mock instances into the fields of the class under test that are annotated with @InjectMocks, allowing the class to be tested with mocked dependencies.
// Support for Additional Mockito Features:
// The extension also supports other Mockito annotations like @Captor (for capturing method arguments) and @Spy (for partial mocking of real objects).
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FinancialTransactionCreateServiceImplTest {
    private static final Long ID_1L = 1L;
    private static final String DESCRIPTION = "Description";
    private static final Instant DATE_NOW = Instant.now();
    private static final Long CATEGORY_ID = 1L;
    private static final Long WALLET_ID_1L = 1L;


    @Mock
    private FinancialTransactionRepository ftRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private FinancialTransactionModelMapper financialTransactionModelMapper;

    @Mock
    private FinancialTransactionCategoryRepository financialTransactionCategoryRepository;

    @InjectMocks
    private FinancialTransactionServiceImpl financialTransactionService;

    @Test
    @DisplayName("Should returns financial transaction for valid parameters")
    void CreateFinancialTransaction_ValidParameters_ReturnsFinancialTransaction() {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();

        Wallet wallet = new Wallet();
        when(walletRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(wallet));

        FinancialTransaction financialTransactionEntity = createFinancialTransactionEntity(ID_1L);
        when(ftRepository.save(Mockito.any(FinancialTransaction.class))).thenReturn(financialTransactionEntity);

        FinancialTransactionDTO financialTransactionDTO = createFinancialTransactionDTO();
        when(financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                any(FinancialTransaction.class))).thenReturn(financialTransactionDTO);

        FinancialTransactionCategory financialTransactionCategory = TestUtils.createTransactionCategory(
                ID_1L, EXPENSE);
        when(financialTransactionCategoryRepository.findByIdAndUserId(any(), any())).thenReturn(
                Optional.ofNullable(financialTransactionCategory));

        // when
        FinancialTransactionDTO result = financialTransactionService.createFinancialTransaction(
                financialTransactionCreateDTO, ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(financialTransactionDTO, result),
                () -> assertEquals(financialTransactionDTO.getId(), result.getId()));
        verify(ftRepository, atMostOnce()).save(any(FinancialTransaction.class));
        verify(walletRepository, atMostOnce()).findByIdAndUserId(any(), any());
        verify(financialTransactionModelMapper, atMostOnce()).mapFinancialTransactionEntityToFinancialTransactionDTO(
                any(FinancialTransaction.class));
        verify(financialTransactionCategoryRepository, atMostOnce()).findByIdAndUserId(any(), any());

    }

    @Test
    @DisplayName("Should throw an exception when category type doesn't match the category type")
    void CreateFinancialTransaction_TypeMismatchCategory_ThrowAppRuntimeException() {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();

        Wallet wallet = new Wallet();
        when(walletRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(wallet));

        FinancialTransaction financialTransactionEntity = createFinancialTransactionEntity(ID_1L);
        when(ftRepository.save(Mockito.any(FinancialTransaction.class))).thenReturn(financialTransactionEntity);

        FinancialTransactionDTO financialTransactionDTO = createFinancialTransactionDTO();
        when(financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                any(FinancialTransaction.class))).thenReturn(financialTransactionDTO);

        FinancialTransactionCategory financialTransactionCategory = TestUtils.createTransactionCategory(
                ID_1L, INCOME);
        when(financialTransactionCategoryRepository.findByIdAndUserId(any(), eq(ID_1L))).thenReturn(
                Optional.ofNullable(financialTransactionCategory));

        // when
        AppRuntimeException exception = assertThrows(AppRuntimeException.class, () ->
                financialTransactionService.createFinancialTransaction(financialTransactionCreateDTO, ID_1L));

        // then
        assertEquals(ErrorCode.FT002.getHttpStatus(), exception.getHttpStatusCode());
        assertEquals(ErrorCode.FT002.getBusinessMessage(), exception.getMessage());

    }

    @Test
    @DisplayName("Shouldn't create financial transaction without existing wallet and throw an exception")
    void CreateFinancialTransaction_WalletNotExist_ThrowAppRuntimeException() {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();

        when(walletRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

        // when
        AppRuntimeException exception = assertThrows(AppRuntimeException.class, () ->
                financialTransactionService.createFinancialTransaction(financialTransactionCreateDTO, ID_1L));

        // then
        assertEquals(ErrorCode.W001.getHttpStatus(), exception.getHttpStatusCode());
        verify(walletRepository, never()).save(any());

    }

    @Test
    @DisplayName("Should create financial transaction with empty description")
    void CreateFinancialTransaction_EmptyDescription_ReturnFinancialTransactionDTOWithEmptyDescription() {
        // given
        FinancialTransactionCreateDTO financialTransactionCreateDTO = createFinancialTransactionCreateDTO();
        financialTransactionCreateDTO.setDescription(EMPTY);

        Wallet wallet = new Wallet();
        when(walletRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(wallet));

        FinancialTransaction financialTransactionEntity = createFinancialTransactionEntity(ID_1L);
        financialTransactionEntity.setDescription(EMPTY);
        when(ftRepository.save(Mockito.any(FinancialTransaction.class))).thenReturn(financialTransactionEntity);

        FinancialTransactionDTO financialTransactionDTO = createFinancialTransactionDTO();
        financialTransactionDTO.setDescription(EMPTY);
        when(financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                any(FinancialTransaction.class))).thenReturn(financialTransactionDTO);

        FinancialTransactionCategory financialTransactionCategory = TestUtils.createTransactionCategory(
                ID_1L, EXPENSE);
        when(financialTransactionCategoryRepository.findByIdAndUserId(any(), any())).thenReturn(
                Optional.ofNullable(financialTransactionCategory));

        // when
        FinancialTransactionDTO result = financialTransactionService.createFinancialTransaction(
                financialTransactionCreateDTO, ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(EMPTY, financialTransactionDTO.getDescription()),
                () -> assertEquals(financialTransactionDTO, result),
                () -> assertEquals(financialTransactionDTO.getId(), result.getId()));
        verify(ftRepository, atMostOnce()).save(any(FinancialTransaction.class));
        verify(walletRepository, atMostOnce()).findByIdAndUserId(any(), any());
        verify(financialTransactionModelMapper, atMostOnce()).mapFinancialTransactionEntityToFinancialTransactionDTO(
                any(FinancialTransaction.class));
        verify(financialTransactionCategoryRepository, atMostOnce()).findByIdAndUserId(any(), any());

    }


    private FinancialTransaction createFinancialTransactionEntity(Long financialTransactionId) {
        FinancialTransaction financialTransaction = new FinancialTransaction();
        financialTransaction.setId(financialTransactionId);
        financialTransaction.setAmount(ONE);
        financialTransaction.setType(EXPENSE);
        financialTransaction.setDescription(DESCRIPTION);
        financialTransaction.setDate(DATE_NOW);
        return financialTransaction;
    }

    private FinancialTransactionCreateDTO createFinancialTransactionCreateDTO() {
        return new FinancialTransactionCreateDTO(WALLET_ID_1L, ONE, DESCRIPTION, EXPENSE,
                DATE_NOW, CATEGORY_ID);
    }

    private FinancialTransactionDTO createFinancialTransactionDTO() {
        return new FinancialTransactionDTO(ID_1L, ONE, DESCRIPTION, EXPENSE, DATE_NOW, CATEGORY_ID);
    }

}