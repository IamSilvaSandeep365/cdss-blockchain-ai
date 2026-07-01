package com.cdss.backend.dto;

import com.cdss.backend.entity.Patient;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private LocalDate      dateOfBirth;
    private Patient.Gender gender;
    private String         nic;
    private String         phone;
    private String         address;
    private String         bloodGroup;
}
