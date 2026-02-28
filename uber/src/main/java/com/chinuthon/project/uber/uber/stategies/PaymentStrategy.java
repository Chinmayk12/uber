package com.chinuthon.project.uber.uber.stategies;

import com.chinuthon.project.uber.uber.entities.Payment;
import com.chinuthon.project.uber.uber.services.PaymentService;

public interface PaymentStrategy {
    Double PLATFORM_COMMISSION = 0.3;
    void processPayment(Payment payment);
}
