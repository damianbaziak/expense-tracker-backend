package com.example.expensestracker.financialtransaktioncategory.impl;

import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryModelMapper;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionCategoryDeleteServiceImplTest {
    private static final Long CATEGORY_ID_1 = 1L;
    private static final Long ID_1 = 1L;
    private static final String EXAMPLE_CATEGORY_NAME = "Example category name_";

    @Mock
    private FinancialTransactionCategoryRepository financialTransactionCategoryRepository;
    @Mock
    private FinancialTransactionCategoryModelMapper financialTransactionCategoryModelMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FinancialTransactionCategoryServiceImpl financialTransactionCategoryService;

    @Test
    @DisplayName("Should call the delete method once")
    void deleteCategory_categoryExists_returnVoid() {
        // given
        when(financialTransactionCategoryRepository.existsByIdAndUserId(anyLong(), anyLong())).thenReturn(Boolean.TRUE);

        // when
        financialTransactionCategoryService.deleteCategory(CATEGORY_ID_1, ID_1);

        // then
        verify(financialTransactionCategoryRepository, times(1))
                .deleteById(CATEGORY_ID_1);
        verify(financialTransactionCategoryRepository, times(1))
                .existsByIdAndUserId(CATEGORY_ID_1, ID_1);

    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when category not exists")
    void deleteCategory_categoryNotExists_shouldThrowException() {
        // given
        when(financialTransactionCategoryRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Boolean.FALSE);

        // when and then
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class,
                () -> financialTransactionCategoryService.deleteCategory(CATEGORY_ID_1, ID_1));

        Assertions.assertAll(
                () -> assertThat(result.getStatus()).isEqualTo(ErrorCode.FTC001.getBusinessCode()),
                () -> assertThat(result.getMessage()).isEqualTo(ErrorCode.FTC001.getBusinessMessage()),
                () -> verify(financialTransactionCategoryRepository, times(1))
                        .existsByIdAndUserId(CATEGORY_ID_1, ID_1));
    }

}