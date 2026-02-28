package com.chinuthon.project.uber.uber.services;

import com.chinuthon.project.uber.uber.entities.Payment;
import com.chinuthon.project.uber.uber.entities.Ride;
import com.chinuthon.project.uber.uber.entities.enums.PaymentStatus;
import com.chinuthon.project.uber.uber.stategies.PaymentStrategyManager;

public interface PaymentService {
    void processPayment(Ride ride);

    Payment createNewPayment(Ride ride);

    void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus);
}
