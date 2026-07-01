package com.cdss.backend.controller;

import com.cdss.backend.dto.PatientRequest;
import com.cdss.backend.entity.Patient;
import com.cdss.backend.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> create(
            @Valid @RequestBody PatientRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                patientService.createPatient(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getMyPatients(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                patientService.getDoctorPatients(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> search(@RequestParam String name) {
        return ResponseEntity.ok(patientService.searchPatients(name));
    }
}
