package com.cdss.backend.service;

import com.cdss.backend.dto.*;
import com.cdss.backend.entity.*;
import com.cdss.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository      patientRepository;
    private final UserRepository         userRepository;
    private final FlaskService           flaskService;

    public Map<String, Object> createPrescription(
            PrescriptionRequest req, String doctorUsername) {

        // Get doctor and patient
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Call Flask AI service
        //PredictionResponse prediction = flaskService.predict(req.getSymptoms());
        PredictionResponse prediction = flaskService.predict(
                req.getEvidences(), req.getAge(), req.getSex());

        // Build and save prescription
        Prescription prescription = Prescription.builder()
                .patient(patient)
                .doctor(doctor)
                .evidences(req.getEvidences())
                .patientAge(req.getAge())
                .patientSex(req.getSex())
                .predictedDisease(prediction.getPredictedDisease())
                .confidenceScore(prediction.getConfidence())
                .finalDiagnosis(req.getFinalDiagnosis() != null
                        ? req.getFinalDiagnosis()
                        : prediction.getPredictedDisease())
                .prescribedMedication(req.getPrescribedMedication())
                .notes(req.getNotes())
                .recordHash(generateHash(req, prediction))
                .status(Prescription.Status.PENDING)
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        // Return prescription + AI explanation together
        Map<String, Object> response = new HashMap<>();
        response.put("prescription", saved);
        response.put("aiPrediction",  prediction);
        return response;
    }

    public List<Prescription> getDoctorPrescriptions(String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return prescriptionRepository.findByDoctorOrderByCreatedAtDesc(doctor);
    }

    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
    }

    public Prescription updateBlockchainTxHash(Long id, String txHash) {
        Prescription prescription = getPrescriptionById(id);
        prescription.setBlockchainTxHash(txHash);
        prescription.setStatus(Prescription.Status.BLOCKCHAIN_STORED);
        return prescriptionRepository.save(prescription);
    }

    // Generate SHA-256 hash of prescription data (used for blockchain later)
    private String generateHash(PrescriptionRequest req,
                                PredictionResponse prediction) {
        try {
            String data = req.getPatientId()
                    + req.getEvidences().toString()
                    + prediction.getPredictedDisease()
                    + prediction.getConfidence()
                    + System.currentTimeMillis();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}
