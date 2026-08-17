package com.cdss.backend.controller;

import com.cdss.backend.dto.PrescriptionRequest;
import com.cdss.backend.entity.Prescription;
import com.cdss.backend.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.cdss.backend.service.BlockchainService;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody PrescriptionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                prescriptionService.createPrescription(
                        request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getMyPrescriptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                prescriptionService.getDoctorPrescriptions(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    private final BlockchainService blockchainService;

    @PostMapping("/{id}/blockchain")
    public ResponseEntity<Map<String, Object>> storeOnBlockchain(
            @PathVariable Long id) {

        Prescription prescription = prescriptionService.getPrescriptionById(id);

        String txHash = blockchainService.storePrescription(
                prescription.getId().toString(),
                prescription.getRecordHash());

        // Update prescription with tx hash
        prescriptionService.updateBlockchainTxHash(id, txHash);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("prescriptionId",  id);
        response.put("recordHash",      prescription.getRecordHash());
        response.put("txHash",          txHash);
        response.put("status",          "BLOCKCHAIN_STORED");
        response.put("message",         "Prescription anchored on blockchain ✅");

        return ResponseEntity.ok(response);
    }

    // ============================================================
// Verify prescription against blockchain
// GET /api/prescriptions/{id}/verify
// ============================================================
    @GetMapping("/{id}/verify")
    public ResponseEntity<Map<String, Object>> verifyOnBlockchain(
            @PathVariable Long id) {

        Prescription prescription = prescriptionService.getPrescriptionById(id);

        boolean isValid = blockchainService.verifyPrescription(
                prescription.getId().toString(),
                prescription.getRecordHash());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("prescriptionId",  id);
        response.put("recordHash",      prescription.getRecordHash());
        response.put("blockchainMatch", isValid);
        response.put("status",          isValid ? "VERIFIED ✅" : "TAMPERED ❌");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Prescription> prescriptions =
                prescriptionService.getDoctorPrescriptions(userDetails.getUsername());

        long total        = prescriptions.size();
        long onBlockchain = prescriptions.stream()
                .filter(p -> p.getBlockchainTxHash() != null)
                .count();

        // Count prescriptions per disease (for a simple breakdown)
        Map<String, Long> byDisease = new LinkedHashMap<>();
        for (Prescription p : prescriptions) {
            String d = p.getPredictedDisease();
            if (d != null) {
                byDisease.merge(d, 1L, Long::sum);
            }
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPrescriptions", total);
        stats.put("onBlockchain",       onBlockchain);
        stats.put("pending",            total - onBlockchain);
        stats.put("byDisease",          byDisease);
        return ResponseEntity.ok(stats);
    }
}