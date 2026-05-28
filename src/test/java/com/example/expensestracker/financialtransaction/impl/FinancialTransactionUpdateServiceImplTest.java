package com.example.expensestracker.financialtransaction.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionUpdateDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.INCOME;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionUpdateServiceImplTest {
    private static final Long TRANSACTION_ID_1L = 1L;
    private static final Long CATEGORY_ID_1L = 1L;
    private static final Long USER_ID_1L = 1L;
    private static final String NEW_DESCRIPTION = "Updated description";
    private static final Instant NEW_DATE = Instant.parse("2024-12-22T14:30:00.500Z");
    private static final BigDecimal NEW_AMOUNT = TEN;

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @Mock
    private FinancialTransactionCategoryRepository financialTransactionCategoryRepository;

    @Mock
    private FinancialTransactionModelMapper financialTransactionModelMapper;

    @InjectMocks
    private FinancialTransactionServiceImpl financialTransactionService;


    @Test
    @DisplayName("Should update financial transaction and return updated DTO")
    void updateFinancialTransaction_validParameters_returnFinancialTransactionDTO() {
        // given
        FinancialTransactionCategory financialTransactionCategory = TestUtils.createTransactionCategory(
                CATEGORY_ID_1L, EXPENSE);

        FinancialTransactionUpdateDTO transactionUpdateDTO = createTransactionUpdateDTO();
        FinancialTransaction existingTransaction = TestUtils.createTransactionForTest(TRANSACTION_ID_1L, EXPENSE);
        FinancialTransactionDTO expectedDTO = createExpectedDTO();

        when(financialTransactionRepository.findByIdAndWalletUserId(TRANSACTION_ID_1L, USER_ID_1L)).thenReturn(
                Optional.of(existingTransaction));

        when(financialTransactionCategoryRepository.findByIdAndUserId(TRANSACTION_ID_1L, USER_ID_1L)).thenReturn(
                Optional.of(financialTransactionCategory));

        when(financialTransactionModelMapper.mapFinancialTransactionEntityToFinancialTransactionDTO(
                existingTransaction)).thenReturn(expectedDTO);

        // when
        FinancialTransactionDTO result = financialTransactionService.updateFinancialTransaction
                (TRANSACTION_ID_1L, transactionUpdateDTO, USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(expectedDTO, result),
                () -> assertEquals(expectedDTO.getId(), result.getId()),
                () -> assertEquals(expectedDTO.getAmount(), result.getAmount()),
                () -> assertEquals(expectedDTO.getDescription(), result.getDescription()));
        verify(financialTransactionCategoryRepository, atMostOnce()).findByIdAndUserId(any(), any());
        verify(financialTransactionModelMapper, atMostOnce())
                .mapFinancialTransactionEntityToFinancialTransactionDTO(any());

    }


    @Test
    @DisplayName("Should throw an exception when updating transaction not found")
    void updateFinancialTransaction_transactionNotFound_shouldTrowException() {
        // given
        FinancialTransactionUpdateDTO transactionUpdateDTO = createTransactionUpdateDTO();

        when(financialTransactionRepository.findByIdAndWalletUserId(TRANSACTION_ID_1L, USER_ID_1L))
                .thenReturn(Optional.empty());

        // when
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> financialTransactionService.updateFinancialTransaction(
                        TRANSACTION_ID_1L, transactionUpdateDTO, USER_ID_1L));

        // then
        assertEquals(ErrorCode.FT001.getBusinessMessage(), result.getMessage());
        assertEquals(ErrorCode.FT001.getHttpStatus(), result.getHttpStatusCode());
        verify(financialTransactionModelMapper, never()).mapFinancialTransactionEntityToFinancialTransactionDTO(any());
        verify(financialTransactionCategoryRepository, never()).findByIdAndUserId(any(), any());

    }

    private FinancialTransactionUpdateDTO createTransactionUpdateDTO() {
        return new FinancialTransactionUpdateDTO(NEW_AMOUNT, NEW_DESCRIPTION, EXPENSE,
                NEW_DATE, TRANSACTION_ID_1L);
    }

    private static FinancialTransactionDTO createExpectedDTO() {
        return new FinancialTransactionDTO(
                TRANSACTION_ID_1L, NEW_AMOUNT, NEW_DESCRIPTION, INCOME, NEW_DATE, CATEGORY_ID_1L);
    }
}
