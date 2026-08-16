package com.cdss.backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PredictionResponse {
    private String                    predictedDisease;
    private Double                    confidence;
    private List<Map<String, Object>> alternatives;
    private List<Map<String, Object>> explanation;
    private List<String>              validEvidences;
    private List<String>              invalidEvidences;
}