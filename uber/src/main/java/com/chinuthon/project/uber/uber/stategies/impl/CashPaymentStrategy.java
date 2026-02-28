package com.chinuthon.project.uber.uber.stategies.impl;

import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Payment;
import com.chinuthon.project.uber.uber.entities.enums.PaymentStatus;
import com.chinuthon.project.uber.uber.entities.enums.TransactionMethod;
import com.chinuthon.project.uber.uber.repositories.PaymentRepository;
import com.chinuthon.project.uber.uber.services.PaymentService;
import com.chinuthon.project.uber.uber.services.WalletService;
import com.chinuthon.project.uber.uber.stategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.stereotype.Service;

// Rider -> 100
// Driver -> 70 Deduct & 30 RS commission to uber from users wallet
@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {
    private final WalletService walletService;
    private final PaymentRepository paymentRepository;
    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount()*PLATFORM_COMMISSION;

        walletService.deductMoneyFromWallet(driver.getUser(),platformCommission,null,
                payment.getRide(), TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
