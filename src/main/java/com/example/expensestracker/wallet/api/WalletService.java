package com.example.expensestracker.wallet.api;

import com.example.expensestracker.wallet.api.dto.WalletCreateDTO;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.dto.WalletUpdateDTO;
import org.springframework.validation.annotation.Validated;

import java.util.List;
@Validated
public interface WalletService {
    WalletDTO createWallet(WalletCreateDTO createWalletDTO, Long userId);

    void deleteWallet(Long walletId, Long userId);

    WalletDTO updateWallet(Long walletId, WalletUpdateDTO updateWalletDTO, Long userId);

    WalletDTO findById(Long walletId, Long UserId);

    List<WalletDTO> findAllWallets(Long userId);

    List<WalletDTO> findAllByNameIgnoreCase(String name, Long userId);
}
