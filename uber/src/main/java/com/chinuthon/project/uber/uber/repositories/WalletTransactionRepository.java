package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Wallet;
import com.chinuthon.project.uber.uber.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {

}
