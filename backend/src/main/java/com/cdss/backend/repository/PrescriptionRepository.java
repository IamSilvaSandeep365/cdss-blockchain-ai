package com.cdss.backend.repository;

import com.cdss.backend.entity.Prescription;
import com.cdss.backend.entity.User;
import com.cdss.backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByDoctor(User doctor);
    List<Prescription> findByPatient(Patient patient);
    List<Prescription> findByDoctorOrderByCreatedAtDesc(User doctor);
    List<Prescription> findByBlockchainTxHashIsNotNull();
}
