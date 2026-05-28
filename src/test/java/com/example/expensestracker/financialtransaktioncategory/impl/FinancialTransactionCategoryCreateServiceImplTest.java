package com.example.expensestracker.financialtransaktioncategory.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryModelMapper;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryCreateDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionCategoryCreateServiceImplTest {

    private static final Long USER_ID_1L = 1L;
    private static final Long ID_1L = 1L;
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
    @DisplayName("Should returns financial transaction category DTO for valid parameters")
    void createCategory_withValidParameters_shouldReturnsFinancialTransactionCategory() {
        // given
        FinancialTransactionCategoryCreateDTO financialTransactionCategoryCreateDTO =
                new FinancialTransactionCategoryCreateDTO(EXAMPLE_CATEGORY_NAME, EXPENSE);

        FinancialTransactionCategory financialTransactionCategoryEntity =
                TestUtils.createTransactionCategory(ID_1L, EXPENSE);

        FinancialTransactionCategoryDTO financialTransactionCategoryDTO
                = TestUtils.createFinancialTransactionCategoryDTO(EXPENSE, USER_ID_1L);

        when(financialTransactionCategoryRepository.save(
                any(FinancialTransactionCategory.class))).thenReturn(financialTransactionCategoryEntity);

        when(financialTransactionCategoryModelMapper
                .mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(
                        any(FinancialTransactionCategory.class))).thenReturn(financialTransactionCategoryDTO);

        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.of(new User()));

        // when
        FinancialTransactionCategoryDTO result = financialTransactionCategoryService.createCategory(
                financialTransactionCategoryCreateDTO, USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(financialTransactionCategoryCreateDTO.getName(), result.getName()),
                () -> assertEquals(financialTransactionCategoryCreateDTO.getType(), result.getType()),
                () -> assertEquals(1L, result.getUserId()));
        verify(financialTransactionCategoryRepository, times(1)).save(argThat(cat ->
                cat.getName().equals(EXAMPLE_CATEGORY_NAME) &&
                        cat.getType().equals(EXPENSE)));
        verify(financialTransactionCategoryModelMapper, times(1))
                .mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(
                        any(FinancialTransactionCategory.class));
    }

    @Test
    @DisplayName("Should returns an AppRuntimeException")
    void createCategory_userNotFound_shouldReturnsAppRuntimeException() {
        // given
        FinancialTransactionCategoryCreateDTO financialTransactionCategoryCreateDTO =
                new FinancialTransactionCategoryCreateDTO(EXAMPLE_CATEGORY_NAME, EXPENSE);
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = Assertions.assertThrows(AppRuntimeException.class, () ->
                financialTransactionCategoryService.createCategory(financialTransactionCategoryCreateDTO, USER_ID_1L));

        Assertions.assertAll(
                () -> assertEquals(ErrorCode.U003.getHttpStatus(), result.getHttpStatusCode()),
                () -> assertEquals(ErrorCode.U003.getBusinessMessage(), result.getMessage()));
        verify(financialTransactionCategoryRepository, never()).save(any(FinancialTransactionCategory.class));
        verify(financialTransactionCategoryModelMapper, never())
                .mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(
                        any(FinancialTransactionCategory.class));
    }


}
