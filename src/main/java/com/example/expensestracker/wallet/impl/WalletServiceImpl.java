package com.example.expensestracker.wallet.impl;

import com.example.expensestracker.financialtransaction.api.FinancialTransactionModelMapper;
import com.example.expensestracker.financialtransaction.api.FinancialTransactionRepository;
import com.example.expensestracker.financialtransaction.api.dto.FinancialTransactionDTO;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import com.example.expensestracker.financialtransaction.api.model.FinancialTransactionType;
import com.example.expensestracker.general.exception.AppRuntimeException;
import com.example.expensestracker.general.exception.ErrorCode;
import com.example.expensestracker.user.api.UserRepository;
import com.example.expensestracker.user.api.model.User;
import com.example.expensestracker.wallet.api.WalletModelMapper;
import com.example.expensestracker.wallet.api.WalletRepository;
import com.example.expensestracker.wallet.api.WalletService;
import com.example.expensestracker.wallet.api.dto.WalletCreateDTO;
import com.example.expensestracker.wallet.api.dto.WalletDTO;
import com.example.expensestracker.wallet.api.dto.WalletUpdateDTO;
import com.example.expensestracker.wallet.api.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletModelMapper walletModelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    @Autowired
    private FinancialTransactionModelMapper transactionModelMapper;

    @Override
    public WalletDTO createWallet(WalletCreateDTO createWalletDTO, Long userId) {
        User walletOwner = getUserByUserId(userId);
        String walletName = createWalletDTO.getName();

        Wallet wallet = new Wallet(walletName, walletOwner);

        Wallet savedWallet = walletRepository.save(wallet);

        return walletModelMapper.mapWalletEntityToWalletDTO(savedWallet);
    }

    @Override
    public void deleteWallet(Long walletId, Long userId) {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new AppRuntimeException(ErrorCode.W001, String.format("Wallet with this id: %d not exist", walletId));
        }
        if (!wallet.get().getUser().getId().equals(userId)) {
            throw new AppRuntimeException(ErrorCode.W002, "You don't have permissions to delete that wallet");
        }

        walletRepository.deleteById(walletId);
    }

    @Override
    @Transactional
    public WalletDTO updateWallet(Long walletId, WalletUpdateDTO updateWalletDTO, Long userId) {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new AppRuntimeException(ErrorCode.W001, String.format("Wallet with this id: %d not exist", walletId));
        }
        if (!wallet.get().getUser().getId().equals(userId)) {
            throw new AppRuntimeException(ErrorCode.W002, "You don't have permissions to update that wallet");
        }

        Wallet existedWallet = wallet.get();
        existedWallet.setName(updateWalletDTO.getName());


        List<FinancialTransaction> transactions =
                financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(
                        existedWallet.getId(), userId);
        List<FinancialTransactionDTO> transactionDTOs = transactions
                .stream()
                .map(transactionModelMapper::mapFinancialTransactionEntityToFinancialTransactionDTO)
                .toList();
        BigDecimal balance = calculateCurrentBalance(transactionDTOs);

        walletRepository.save(existedWallet);

        return new WalletDTO(existedWallet.getId(), existedWallet.getName(), existedWallet.getCreationDate(),
                existedWallet.getUser().getId(), balance);
    }

    @Override
    public WalletDTO findById(Long walletId, Long userId) {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new AppRuntimeException(ErrorCode.W001, String.format("Wallet with this id: %d not exist", walletId));
        }
        if (!wallet.get().getUser().getId().equals(userId)) {
            throw new AppRuntimeException(ErrorCode.W002, "You don't have permissions to view that wallet");
        }

        Wallet existedWallet = wallet.get();

        List<FinancialTransaction> transactions =
                financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(
                        existedWallet.getId(), userId);
        List<FinancialTransactionDTO> transactionDTOs = transactions
                .stream()
                .map(transactionModelMapper::mapFinancialTransactionEntityToFinancialTransactionDTO)
                .toList();
        BigDecimal balance = calculateCurrentBalance(transactionDTOs);

        return new WalletDTO(existedWallet.getId(), existedWallet.getName(), existedWallet.getCreationDate(),
                existedWallet.getUser().getId(), balance);
    }

    @Override
    public List<WalletDTO> findAllWallets(Long userId) {
        List<Wallet> walletList = walletRepository.findAllByUserIdOrderByNameAsc(userId);

        if (walletList.isEmpty()) {
            throw new AppRuntimeException(ErrorCode.W001, "You have no wallets");
        }

        return walletList.stream()
                .map(wallet -> {
                    List<FinancialTransaction> transactions =
                            financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(
                                    wallet.getId(), userId);
                    List<FinancialTransactionDTO> transactionDTOs = transactions
                            .stream()
                            .map(transactionModelMapper::mapFinancialTransactionEntityToFinancialTransactionDTO)
                            .toList();
                    BigDecimal balance = calculateCurrentBalance(transactionDTOs);

                    return new WalletDTO(wallet.getId(), wallet.getName(), wallet.getCreationDate(),
                            wallet.getUser().getId(), balance);
                })
                .toList();
    }


    @Override
    public List<WalletDTO> findAllByNameLikeIgnoreCase(String name, Long userId) {
        return walletRepository.findAllByUserIdAndNameIsContainingIgnoreCase(userId, name)
                .stream()
                .map(wallet -> {
                    List<FinancialTransaction> transactions =
                            financialTransactionRepository.findAllByWalletIdAndWalletUserIdOrderByDateDesc(
                                    wallet.getId(), userId);
                    List<FinancialTransactionDTO> transactionDTOs = transactions
                            .stream()
                            .map(transactionModelMapper::mapFinancialTransactionEntityToFinancialTransactionDTO)
                            .toList();
                    BigDecimal balance = calculateCurrentBalance(transactionDTOs);
                    return new WalletDTO(wallet.getId(), wallet.getName(), wallet.getCreationDate(),
                            wallet.getUser().getId(), balance);
                })
                .toList();


    }

    private BigDecimal calculateCurrentBalance(List<FinancialTransactionDTO> transactionDTOs) {
        return transactionDTOs.stream()
                .map(transaction -> transaction.getType() == FinancialTransactionType.INCOME
                        ? transaction.getAmount()
                        : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new AppRuntimeException(ErrorCode.U003, String.format("User with id: %d doesn't exist.", userId)));
    }
}
