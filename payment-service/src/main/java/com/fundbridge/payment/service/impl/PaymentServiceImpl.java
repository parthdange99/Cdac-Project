package com.fundbridge.payment.service.impl;

import com.fundbridge.common.enums.PaymentStatus;
import com.fundbridge.common.enums.TransactionType;
import com.fundbridge.common.exception.BadRequestException;
import com.fundbridge.payment.config.RazorpayConfig;
import com.fundbridge.payment.entity.Transaction;
import com.fundbridge.payment.repository.TransactionRepository;
import com.fundbridge.payment.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayConfig razorpayConfig;
    private final TransactionRepository transactionRepository;

    @Override
    public Map<String, Object> createOrder(double amount, String description, String email, Long userId) {
        try {
            RazorpayClient client = new RazorpayClient(
                    razorpayConfig.getKeyId(), razorpayConfig.getKeySecret());

            JSONObject options = new JSONObject();
            options.put("amount", Math.round(amount * 100)); // in paise
            options.put("currency", "INR");
            options.put("receipt", "receipt_" + UUID.randomUUID().toString().substring(0, 12));

            JSONObject notes = new JSONObject();
            notes.put("description", description);
            notes.put("userEmail", email);
            options.put("notes", notes);

            Order order = client.orders.create(options);

            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId", order.get("id"));
            orderDetails.put("amount", order.get("amount"));
            orderDetails.put("currency", order.get("currency"));
            orderDetails.put("description", description);
            orderDetails.put("keyId", razorpayConfig.getKeyId());

            return orderDetails;
        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new BadRequestException("Could not create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Transaction recordTransaction(String razorpayOrderId, String razorpayPaymentId,
                                         double amount, String description, String email, Long userId) {
        Transaction transaction = Transaction.builder()
                .type(TransactionType.DONATION)
                .amount(BigDecimal.valueOf(amount))
                .razorpayPaymentId(razorpayPaymentId)
                .razorpayOrderId(razorpayOrderId)
                .paymentStatus(PaymentStatus.SUCCESS)
                .description(description)
                .userId(userId)
                .userEmail(email)
                .build();
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionHistory(String email) {
        return transactionRepository.findByUserEmail(email);
    }

    @Override
    public String getPublicKey() {
        return razorpayConfig.getKeyId();
    }
}
