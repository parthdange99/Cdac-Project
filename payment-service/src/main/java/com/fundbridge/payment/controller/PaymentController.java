package com.fundbridge.payment.controller;

import com.fundbridge.common.dto.response.ApiResponse;
import com.fundbridge.payment.entity.Transaction;
import com.fundbridge.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @RequestParam double amount,
            @RequestParam(defaultValue = "FundBridge Payment") String description,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> order = paymentService.createOrder(amount, description, email, userId);
        return ResponseEntity.ok(ApiResponse.success("Order created", order));
    }

    @PostMapping("/record")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recordTransaction(
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            @RequestParam double amount,
            @RequestParam(defaultValue = "Payment") String description,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId) {
        Transaction tx = paymentService.recordTransaction(
                razorpayOrderId, razorpayPaymentId, amount, description, email, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("transactionId", tx.getId());
        result.put("status", tx.getPaymentStatus());
        return ResponseEntity.ok(ApiResponse.success("Transaction recorded", result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Transaction>>> getHistory(
            @RequestHeader("X-User-Email") String email) {
        List<Transaction> history = paymentService.getTransactionHistory(email);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/key")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPublicKey() {
        Map<String, String> key = new HashMap<>();
        key.put("keyId", paymentService.getPublicKey());
        return ResponseEntity.ok(ApiResponse.success(key));
    }
}
