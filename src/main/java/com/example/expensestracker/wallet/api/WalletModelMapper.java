package com.example.expensestracker.wallet.api;

import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletModelMapper {

    @Mapping(source = "user.id", target = "userId")
    WalletDTO mapWalletEntityToWalletDTO(Wallet wallet);
}
