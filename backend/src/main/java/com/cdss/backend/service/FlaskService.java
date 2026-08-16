package com.cdss.backend.service;

import com.cdss.backend.dto.PredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlaskService {

    @Value("${flask.api.url}")
    private String flaskUrl;

    private final WebClient.Builder webClientBuilder;

    public PredictionResponse predict(List<String> evidences, Integer age, String sex) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("evidences", evidences);
            requestBody.put("age", age);
            requestBody.put("sex", sex);

            Map response = webClientBuilder.build()
                    .post()
                    .uri(flaskUrl + "/predict")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            PredictionResponse result = new PredictionResponse();

            if (response != null && "success".equals(response.get("status"))) {
                Map<?, ?> prediction = (Map<?, ?>) response.get("prediction");
                result.setPredictedDisease((String) prediction.get("disease"));
                result.setConfidence(
                        ((Number) prediction.get("confidence")).doubleValue());
                result.setAlternatives(
                        (List<Map<String, Object>>) prediction.get("alternatives"));
                result.setExplanation(
                        (List<Map<String, Object>>) response.get("explanation"));
                result.setValidEvidences(
                        (List<String>) response.get("valid_evidences"));
                result.setInvalidEvidences(
                        (List<String>) response.get("invalid_evidences"));
            }

            return result;

        } catch (Exception e) {
            log.error("Flask API call failed: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage());
        }
    }

    public boolean isFlaskHealthy() {
        try {
            Map response = webClientBuilder.build()
                    .get()
                    .uri(flaskUrl + "/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response != null && "UP".equals(response.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}