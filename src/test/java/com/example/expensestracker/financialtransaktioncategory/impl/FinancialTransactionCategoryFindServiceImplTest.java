package com.example.expensestracker.financialtransaktioncategory.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryModelMapper;
import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryRepository;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDetailedDTO;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType.EXPENSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionCategoryFindServiceImplTest {

    private static final Long USER_ID_1L = 1L;
    private static final Long CATEGORY_ID_1L = 1L;
    private static final Long CATEGORY_ID_2L = 2L;
    private static final Long CATEGORY_ID_3L = 3L;
    private static final String EXAMPLE_CATEGORY_NAME_1 = "Example category name_1";
    private static final String EXAMPLE_CATEGORY_NAME_2 = "Example category name_2";
    private static final String EXAMPLE_CATEGORY_NAME_3 = "Example category name_3";
    private static final BigInteger NUMBER_OF_FINANCIAL_TRANSACTIONS = new BigInteger("4");
    @Mock
    private FinancialTransactionCategoryRepository financialTransactionCategoryRepository;
    @InjectMocks
    private FinancialTransactionCategoryServiceImpl financialTransactionCategoryService;
    @Mock
    private FinancialTransactionRepository financialTransactionRepository;
    @Mock
    private FinancialTransactionCategoryModelMapper financialTransactionCategoryModelMapper;


    @Test
    @DisplayName("Should returns financial transaction category detailed DTO")
    void findFinancialTransactionCategoryForUser_transactionCategoryExist_returnsFTCDetailedDTO() {
        // given
        FinancialTransactionCategory financialTransactionCategory = TestUtils.createTransactionCategory(
                CATEGORY_ID_1L, EXPENSE);
        when(financialTransactionCategoryRepository.findByIdAndUserId(CATEGORY_ID_1L, USER_ID_1L))
                .thenReturn(Optional.of(financialTransactionCategory));

        when(financialTransactionRepository.countFinancialTransactionsByFinancialTransactionCategoryId(CATEGORY_ID_1L))
                .thenReturn(NUMBER_OF_FINANCIAL_TRANSACTIONS);

        FinancialTransactionCategoryDTO financialTransactionCategoryDTO =
                TestUtils.createFinancialTransactionCategoryDTO(EXPENSE, USER_ID_1L);
        when(financialTransactionCategoryModelMapper
                .mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(financialTransactionCategory))
                .thenReturn(financialTransactionCategoryDTO);

        // when
        FinancialTransactionCategoryDetailedDTO result =
                financialTransactionCategoryService.findFinancialTransactionCategoryForUser(1L, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.financialTransactionCategoryDTO()).isEqualTo(financialTransactionCategoryDTO);
        assertThat(result.financialTransactionCounter()).isEqualTo(NUMBER_OF_FINANCIAL_TRANSACTIONS);

    }

    @Test
    @DisplayName("Should throws an AppRuntimeException when financial category not found")
    void findFinancialTransactionCategoryForUser_categoryNotFound_throwsAppRuntimeException() {
        // given
        when(financialTransactionCategoryRepository.findByIdAndUserId(CATEGORY_ID_1L, USER_ID_1L))
                .thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(AppRuntimeException.class, () -> financialTransactionCategoryService
                .findFinancialTransactionCategoryForUser(CATEGORY_ID_1L, USER_ID_1L));

        assertThat(result).hasMessage(ErrorCode.FTC001.getBusinessMessage());
        assertThat(result.getHttpStatusCode()).isEqualTo(ErrorCode.FTC001.getHttpStatus());
        assertThat(result.getBusinessCode()).isEqualTo((ErrorCode.FTC001.getBusinessCode()));

        // Verification that not unnecessary operation were called
        verify(financialTransactionRepository, times(0))
                .countFinancialTransactionsByFinancialTransactionCategoryId(any());
        verify(financialTransactionCategoryModelMapper, times(0))
                .mapFinancialTransactionCategoryEntityToFinancialTransactionCategoryDTO(
                        any(FinancialTransactionCategory.class));
    }

    @Test
    @DisplayName("Should return a list of FinancialTransactionCategoryDTOs")
    void findFinancialTransactionCategories_categoriesExist_shouldReturnCategoriesDTOsList() {
        // given
        User user = TestUtils.createUser();

        List<FinancialTransactionCategory> financialTransactionCategories =
                TestUtils.createTransactionCategories(3, EXPENSE, user);
        when(financialTransactionCategoryRepository.findAllByUserId(USER_ID_1L)).thenReturn(
                financialTransactionCategories);

        List<FinancialTransactionCategoryDTO> financialTransactionCategoryDTOList =
                TestUtils.createTransactionCategoriesDTOS(3, EXPENSE, USER_ID_1L);

        // when
        List<FinancialTransactionCategoryDTO> result =
                financialTransactionCategoryService.findFinancialTransactionCategories(USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).containsExactlyInAnyOrderElementsOf(financialTransactionCategoryDTOList),
                () -> verify(financialTransactionCategoryRepository, times(1)).findAllByUserId(
                        USER_ID_1L));

        /*
                () -> assertThat(result.get(0).getId()).isEqualTo(CATEGORY_ID_1L),
                () -> assertThat(result.get(1).getId()).isEqualTo(CATEGORY_ID_2L),
                () -> assertThat(result.get(2).getId()).isEqualTo(CATEGORY_ID_3L),
                () -> assertThat(result.get(0).getName()).isEqualTo(EXAMPLE_CATEGORY_NAME_1),
                () -> assertThat(result.get(1).getName()).isEqualTo(EXAMPLE_CATEGORY_NAME_2),
                () -> assertThat(result.get(2).getName()).isEqualTo(EXAMPLE_CATEGORY_NAME_3));

        */

    }

    @Test
    @DisplayName("Should return an empty List when no categories exist")
    void findFinancialTransactionCategories_noCategoriesExist_shouldReturnEmptyList() {
        // given
        when(financialTransactionCategoryRepository.findAllByUserId(USER_ID_1L)).thenReturn(
                Collections.emptyList());

        // when
        List<FinancialTransactionCategoryDTO> result =
                financialTransactionCategoryService.findFinancialTransactionCategories(USER_ID_1L);

        // then
        Assertions.assertAll(
                () -> assertThat(result).isEmpty(),
                () -> verify(financialTransactionCategoryRepository, times(1)).findAllByUserId(
                        USER_ID_1L));


    }
}

