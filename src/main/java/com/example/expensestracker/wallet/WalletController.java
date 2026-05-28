package com.example.expensestracker.wallet;

import com.example.expensestracker.user.api.UserService;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletService;
import com.example.expensestracker.wallet.api.dto.WalletCreateDTO;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.dto.WalletUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;

    @PostMapping()
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletCreateDTO createWalletDTO,
                                                  Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        WalletDTO walletDTO = walletService.createWallet(createWalletDTO, userId);

        return new ResponseEntity<>(walletDTO, HttpStatus.CREATED);


    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWalletById(@Min(1) @NotNull @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        walletService.deleteWallet(id, userId);

        return new ResponseEntity<>("Wallet deleted successfully", HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WalletDTO> updateWalletName(@Min(1) @NotNull @PathVariable Long id,
                                                       @Valid @RequestBody WalletUpdateDTO walletUpdateDTO, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        WalletDTO walletDTO = walletService.updateWallet(id, walletUpdateDTO, userId);

        return new ResponseEntity<>(walletDTO, HttpStatus.OK);

    }

    @GetMapping()
    public ResponseEntity<List<WalletDTO>> getWallets(Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        List<WalletDTO> wallets = walletService.findAllWallets(userId);

        return new ResponseEntity<>(wallets, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletDTO> getWalletById(@Min(1) @NotNull @PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        WalletDTO walletDTO = walletService.findById(id, userId);

        return new ResponseEntity<>(walletDTO, HttpStatus.OK);

    }

    @GetMapping("/wallets/{name}")
    public ResponseEntity<List<WalletDTO>> getAllByNameLikeIgnoreCase(
            @PathVariable @NotBlank @Pattern(regexp = "[\\w ]+") @Size(min = 2, max = 20)
            String name, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        Long userId = user.getId();

        List<WalletDTO> walletDTOs = walletService.findAllByNameLikeIgnoreCase(name, userId);

        if (walletDTOs.isEmpty()) {
            return new ResponseEntity<>(walletDTOs, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(walletDTOs, HttpStatus.OK);

    }
}
