package com.cdss.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PrescriptionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotEmpty(message = "At least one symptom is required")
    private List<String> symptoms;

    private String finalDiagnosis;
    private String prescribedMedication;
    private String notes;
}
