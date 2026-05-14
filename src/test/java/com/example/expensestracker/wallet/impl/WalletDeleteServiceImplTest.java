package com.example.expensestracker.wallet.impl;

import com.example.expensestracker.TestUtils;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletRepository;
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
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class WalletDeleteServiceImplTest {

    private static final Long WALLET_ID_1L = 1L;

    @InjectMocks
    private WalletServiceImpl walletService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("Should call the delete method once")
    void deleteWallet_walletExists_deletedSuccessfully() {
        // given
        User user = TestUtils.createUserForTest();
        Wallet wallet = TestUtils.createWalletForTest(user);
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));

        // when
        walletService.deleteWallet(WALLET_ID_1L, user.getId());

        // then
        verify(walletRepository, times(1)).findById(WALLET_ID_1L);
        verify(walletRepository, times(1)).deleteById(WALLET_ID_1L);

    }

    @Test
    @DisplayName("Should throw an AppRuntimeException")
    void deleteWallet_walletNotExists_throwAnException() {
        // given
        User user = TestUtils.createUserForTest();
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.empty());

        // when
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> walletService.deleteWallet(WALLET_ID_1L, user.getId()));

        // then
        Assertions.assertAll(
                () -> assertEquals(ErrorCode.W001.getBusinessMessage(), result.getMessage()),
                () -> assertEquals(ErrorCode.W001.getHttpStatus(), result.getHttpStatusCode()),
                () -> assertEquals(ErrorCode.W001.getBusinessCode(), result.getStatus()));
        verify(walletRepository, times(1)).findById(WALLET_ID_1L);
        verify(walletRepository, times(0)).deleteById(WALLET_ID_1L);


    }

    @Test
    @DisplayName("Should throw an AppRuntimeException when user has no permissions to delete that wallet")
    void deleteWallet_userHasNoPermissions_throwAnException() {
        // given
        User user = TestUtils.createUserForTest();
        Wallet wallet = TestUtils.createWalletForTest(user);
        when(walletRepository.findById(WALLET_ID_1L)).thenReturn(Optional.of(wallet));

        // when
        AppRuntimeException result = assertThrows(AppRuntimeException.class,
                () -> walletService.deleteWallet(WALLET_ID_1L, 7L));

        // then
        Assertions.assertAll(
                () -> assertEquals(ErrorCode.W002.getBusinessMessage(), result.getMessage()),
                () -> assertEquals(ErrorCode.W002.getHttpStatus(), result.getHttpStatusCode()),
                () -> assertEquals(ErrorCode.W002.getBusinessCode(), result.getStatus()));
        verify(walletRepository, times(1)).findById(WALLET_ID_1L);
        verify(walletRepository, times(0)).deleteById(WALLET_ID_1L);


    }
}