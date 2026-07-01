package com.cdss.backend.service;

import com.cdss.backend.dto.PatientRequest;
import com.cdss.backend.entity.*;
import com.cdss.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository    userRepository;

    public Patient createPatient(PatientRequest req, String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (req.getNic() != null && patientRepository.existsByNic(req.getNic())) {
            throw new RuntimeException("Patient with this NIC already exists");
        }

        Patient patient = Patient.builder()
                .fullName(req.getFullName())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .nic(req.getNic())
                .phone(req.getPhone())
                .address(req.getAddress())
                .bloodGroup(req.getBloodGroup())
                .createdBy(doctor)
                .build();

        return patientRepository.save(patient);
    }

    public List<Patient> getDoctorPatients(String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return patientRepository.findByCreatedBy(doctor);
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public Patient updatePatient(Long id, PatientRequest req) {
        Patient patient = getPatientById(id);
        patient.setFullName(req.getFullName());
        patient.setDateOfBirth(req.getDateOfBirth());
        patient.setGender(req.getGender());
        patient.setPhone(req.getPhone());
        patient.setAddress(req.getAddress());
        patient.setBloodGroup(req.getBloodGroup());
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> searchPatients(String name) {
        return patientRepository.findByFullNameContainingIgnoreCase(name);
    }
}
