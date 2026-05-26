package com.example.expensestracker.wallet.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletModelMapper;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.dto.WalletCreateDTO;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletCreateServiceImplTest {

    private static final Long USER_ID_1L = 1L;
    private static final Long ID_1L = 1L;
    private static final String EXAMPLE_WALLET_NAME = "Example wallet name_";

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private WalletServiceImpl walletService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletModelMapper walletModelMapper;


    @Test
    @DisplayName("Should create wallet and return walletDTO")
    void createWallet_withValidParameters_shouldReturnWallet() {
        // given
        User walletOwner = TestUtils.createUserForTest();
        Wallet wallet = TestUtils.createWalletForTest(ID_1L, walletOwner);
        WalletDTO walletDTO = TestUtils.createWalletDTOForTest(USER_ID_1L);
        WalletCreateDTO createDTO = new WalletCreateDTO(EXAMPLE_WALLET_NAME);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.of(walletOwner));
        when(walletModelMapper.mapWalletEntityToWalletDTO(wallet)).thenReturn(walletDTO);


        // when
        WalletDTO result = walletService.createWallet(createDTO, USER_ID_1L);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertAll(
                () -> assertEquals(wallet.getId(), result.getId()),
                () -> assertEquals(wallet.getName(), result.getName()),
                () -> assertEquals(wallet.getCreationDate(), result.getCreationDate()),
                () -> assertEquals(wallet.getUser().getId(), result.getUserId())
        );
    }

    @Test
    @DisplayName("Should not create wallet and throw an AppRuntimeException")
    void createWallet_userNotExists_shouldReturnAnException() {
        // given
        WalletCreateDTO createDTO = new WalletCreateDTO(EXAMPLE_WALLET_NAME);
        when(userRepository.findById(USER_ID_1L)).thenReturn(Optional.empty());

        // when and then
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> walletService.createWallet(createDTO, USER_ID_1L));

        Assertions.assertAll(
                () -> assertEquals(ErrorCode.U003.getHttpStatus(), result.getHttpStatusCode()),
                () -> assertEquals(ErrorCode.U003.getBusinessMessage(), result.getMessage()),
                () -> assertEquals(ErrorCode.U003.getBusinessCode(), result.getBusinessCode()),
                () -> verify(walletRepository, never()).save(any(Wallet.class)),
                () -> verify(walletModelMapper, never()).mapWalletEntityToWalletDTO(any(Wallet.class)));

    }
}