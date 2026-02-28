package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.dto.WalletTransactionDto;
import com.chinuthon.project.uber.uber.entities.WalletTransaction;
import com.chinuthon.project.uber.uber.repositories.WalletTransactionRepository;
import com.chinuthon.project.uber.uber.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createNewWalletTransaction(WalletTransaction walletTransaction) {
        WalletTransaction walletTransactionEntity = modelMapper.map(walletTransaction,WalletTransaction.class);
        walletTransactionRepository.save(walletTransactionEntity);
    }
}
