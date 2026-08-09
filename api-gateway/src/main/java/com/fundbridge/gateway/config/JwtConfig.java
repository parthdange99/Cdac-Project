package com.fundbridge.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret:FundBridge_Super_Secret_Key_2026_MicroservicesEdition}")
    private String secret;

    public String getSecret() {
        return secret;
    }
}
