package com.fundbridge.campaign.client;

import com.fundbridge.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    @PostMapping("/notifications/send")
    ResponseEntity<ApiResponse<Object>> sendNotification(@RequestBody Map<String, Object> request);
}
