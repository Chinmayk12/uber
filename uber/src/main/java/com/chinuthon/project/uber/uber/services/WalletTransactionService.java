package com.chinuthon.project.uber.uber.services;


import com.chinuthon.project.uber.uber.dto.WalletTransactionDto;
import com.chinuthon.project.uber.uber.entities.WalletTransaction;

public interface WalletTransactionService {
    void createNewWalletTransaction(WalletTransaction walletTransaction);
}
