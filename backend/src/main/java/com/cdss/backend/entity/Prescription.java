package com.cdss.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    // Symptoms entered by doctor
    @ElementCollection
    @CollectionTable(name = "prescription_symptoms",
            joinColumns = @JoinColumn(name = "prescription_id"))
    @Column(name = "symptom")
    private List<String> symptoms;

    // AI prediction result
    @Column(name = "predicted_disease")
    private String predictedDisease;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    // Doctor's final decision (may differ from AI)
    @Column(name = "final_diagnosis")
    private String finalDiagnosis;

    @Column(name = "prescribed_medication", columnDefinition = "TEXT")
    private String prescribedMedication;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Blockchain fields
    @Column(name = "record_hash")
    private String recordHash;

    @Column(name = "blockchain_tx_hash")
    private String blockchainTxHash;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status    = Status.PENDING;
    }

    public enum Status {
        PENDING, CONFIRMED, BLOCKCHAIN_STORED
    }
}
