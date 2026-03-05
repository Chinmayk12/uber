package com.chinuthon.project.uber.uber.entities;

import com.chinuthon.project.uber.uber.entities.enums.TransactionMethod;
import com.chinuthon.project.uber.uber.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_wallet_transaction_wallet",columnList = "wallet_id"),
        @Index(name = "idx_wallet_transaction_ride",columnList = "ride_id")
})
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionMethod transactionMethod;

    // One Transaction is Associated with One Ride, But Each Ride Can Have Multiple Transactions (e.g., Payment, Refund)
    @ManyToOne
    private Ride ride;

    private String transactionId;

    // One Wallet Can Have Multiple Transactions, But Each Transaction is Associated with One Wallet
    @ManyToOne
    private Wallet wallet;

    @CreationTimestamp
    private LocalDateTime timeStamp;

}
