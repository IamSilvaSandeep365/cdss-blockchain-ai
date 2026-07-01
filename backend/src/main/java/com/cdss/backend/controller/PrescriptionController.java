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
}