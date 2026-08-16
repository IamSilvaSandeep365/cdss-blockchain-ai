package com.cdss.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PrescriptionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotEmpty(message = "At least one evidence is required")
    private List<String> evidences;


    private Integer age;
    private String  sex;

    private String finalDiagnosis;
    private String prescribedMedication;
    private String notes;
}