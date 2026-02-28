package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.User;
import com.chinuthon.project.uber.uber.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Optional<Wallet> findByUser(User user);
}
