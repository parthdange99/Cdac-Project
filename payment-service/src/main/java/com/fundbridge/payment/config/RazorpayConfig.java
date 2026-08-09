package com.fundbridge.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RazorpayConfig {

    @Value("${razorpay.key.id:rzp_test_TH8MltEKux2Yht}")
    private String keyId;

    @Value("${razorpay.key.secret:BxhfgTALeqnbCF6OMTXbNbIN}")
    private String keySecret;
}
