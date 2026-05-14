package com.example.expensestracker.financialtransaction;

import com.example.expensestracker.financialtransaction.api.FinancialTransactionService;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionCreateDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionUpdateDTO;
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
@RequestMapping("/api/transactions")
public class FinancialTransactionController {

    @Autowired
    private FinancialTransactionService financialTransactionService;

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<FinancialTransactionDTO> createFinancialTransaction(
            @Valid @RequestBody FinancialTransactionCreateDTO financialTransactionCreateDTO, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionDTO financialTransactionDTO =
                financialTransactionService.createFinancialTransaction(financialTransactionCreateDTO, userId);

        return new ResponseEntity<>(financialTransactionDTO, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<FinancialTransactionDTO>> getFinancialTransactionsByWalletId(
            @RequestParam @Min(1) @NotNull Long walletId, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        List<FinancialTransactionDTO> financialTransactionDTOS = financialTransactionService
                .findFinancialTransactionsByWalletId(walletId, userId);

        return new ResponseEntity<>(financialTransactionDTOS, HttpStatus.OK);


    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialTransactionDTO> updateTransactionById(
            @Min(1) @NotNull @PathVariable Long id,
            @Valid @RequestBody FinancialTransactionUpdateDTO financialTransactionUpdateDTO, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionDTO financialTransactionDTO = financialTransactionService.updateFinancialTransaction(
                id, financialTransactionUpdateDTO, userId);

        return new ResponseEntity<>(financialTransactionDTO, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialTransactionDTO> getTransactionById(
            @Min(1) @NotNull @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        FinancialTransactionDTO financialTransactionDTO = financialTransactionService.findFinancialTransactionForUser(
                id, userId);

        return new ResponseEntity<>(financialTransactionDTO, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransactionById(@Min(1) @NotNull @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        financialTransactionService.deleteTransaction(id, userId);

        return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);

    }


}
