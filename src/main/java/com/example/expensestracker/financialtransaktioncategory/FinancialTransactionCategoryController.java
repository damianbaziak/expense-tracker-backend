package com.example.expensestracker.financialtransaktioncategory;

import com.example.expensestracker.financialtransaktioncategory.api.FinancialTransactionCategoryService;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryCreateDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryDetailedDTO;
import com.example.expensestracker.financialtransaktioncategory.api.dto.FinancialTransactionCategoryUpdateDTO;
import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/categories")
public class FinancialTransactionCategoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private FinancialTransactionCategoryService financialTransactionCategoryService;

    @PostMapping()
    public ResponseEntity<FinancialTransactionCategoryDTO> createCategory(
            @Valid @RequestBody FinancialTransactionCategoryCreateDTO categoryCreateDTO, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionCategoryDTO categoryDTO =
                financialTransactionCategoryService.createCategory(categoryCreateDTO, userId);

        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialTransactionCategoryDetailedDTO> getFinancialCategoryById(
            @NotNull @Min(1) @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionCategoryDetailedDTO categoryDetailedDTO = financialTransactionCategoryService
                .findFinancialTransactionCategoryForUser(id, userId);

        return new ResponseEntity<>(categoryDetailedDTO, HttpStatus.OK);

    }


    @GetMapping()
    public ResponseEntity<List<FinancialTransactionCategoryDTO>> getFinancialTransactionCategories(
            Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        List<FinancialTransactionCategoryDTO> financialTransactionCategoryDTOList =
                financialTransactionCategoryService.findFinancialTransactionCategories(userId);

        return new ResponseEntity<>(financialTransactionCategoryDTOList, HttpStatus.OK);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<FinancialTransactionCategoryDTO> updateFinancialTransactionCategory(
            @NotNull @Min(1) @PathVariable Long id,
            @Valid @RequestBody FinancialTransactionCategoryUpdateDTO categoryUpdateDTO,
            Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionCategoryDTO financialTransactionCategoryDTO =
                financialTransactionCategoryService.updateFinancialTransactionCategory(
                        id, categoryUpdateDTO, userId);

        return new ResponseEntity<>(financialTransactionCategoryDTO, HttpStatus.OK);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFinancialTransactionCategoryById(
            @NotNull @Min(1) @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        financialTransactionCategoryService.deleteCategory(id, userId);

        return new ResponseEntity<>("Financial transaction category deleted successfully", HttpStatus.OK);

    }



}
