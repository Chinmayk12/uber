package com.chinuthon.project.uber.uber.stategies.impl;

import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Payment;
import com.chinuthon.project.uber.uber.entities.Rider;
import com.chinuthon.project.uber.uber.entities.enums.PaymentStatus;
import com.chinuthon.project.uber.uber.entities.enums.TransactionMethod;
import com.chinuthon.project.uber.uber.repositories.PaymentRepository;
import com.chinuthon.project.uber.uber.services.PaymentService;
import com.chinuthon.project.uber.uber.services.WalletService;
import com.chinuthon.project.uber.uber.stategies.PaymentStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

// Rider had 523 , Driver has 500
// Ride cost is 100 , uber commission is 30
// Rider -> 232-100 = 132
// Driver -> 500 + (100-132) = 570
@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {
    private final WalletService walletService;
    private final PaymentRepository paymentRepository;


    @Override
    @Transactional
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();

        walletService.deductMoneyFromWallet(rider.getUser(),payment.getAmount(),
                null,payment.getRide(), TransactionMethod.RIDE);

        double driversCut = payment.getAmount() * (1- PLATFORM_COMMISSION);

        walletService.addMoneyToWallet(driver.getUser(),driversCut,null,
                payment.getRide(),TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);


    }
}
