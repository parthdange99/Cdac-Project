package com.fundbridge.payment.service;

import com.fundbridge.payment.entity.Transaction;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> createOrder(double amount, String description, String email, Long userId);
    Transaction recordTransaction(String razorpayOrderId, String razorpayPaymentId,
                                  double amount, String description, String email, Long userId);
    List<Transaction> getTransactionHistory(String email);
    String getPublicKey();
}
