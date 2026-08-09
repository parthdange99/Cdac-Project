package com.fundbridge.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private String callGroq(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = Map.of(
                "model", MODEL,
                "messages", List.of(message),
                "max_tokens", 500,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    GROQ_URL, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    return (String) msg.get("content");
                }
            }
        } catch (Exception e) {
            log.error("Groq API call failed: {}", e.getMessage());
            throw new RuntimeException("AI generation failed: " + e.getMessage());
        }

        return "Could not generate content. Please try again.";
    }

    public String generateCampaignDescription(String title, String category) {
        log.info("Generating campaign description for: {}", title);
        String prompt = String.format(
            "You are an expert crowdfunding copywriter. Write a compelling, emotional, and concise " +
            "campaign description (150-200 words) for a crowdfunding campaign titled '%s' in the category '%s'. " +
            "The description should inspire people to donate. Use a warm, personal tone. " +
            "Do NOT include the title itself in the description. Just write the body text.",
            title, category
        );
        return callGroq(prompt);
    }

    public String suggestDonationMessage(String donorName, String campaignTitle) {
        log.info("Generating donation message for donor: {} to campaign: {}", donorName, campaignTitle);
        String name = (donorName != null && !donorName.isBlank()) ? donorName : "a donor";
        String prompt = String.format(
            "Write a short, heartfelt donation message (2-3 sentences max) from '%s' to the campaign '%s'. " +
            "Make it warm, encouraging, and genuine. Just write the message directly, no quotes or labels.",
            name, campaignTitle
        );
        return callGroq(prompt);
    }
}
