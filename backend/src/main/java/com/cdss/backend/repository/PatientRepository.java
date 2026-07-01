package com.cdss.backend.repository;

import com.cdss.backend.entity.Patient;
import com.cdss.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient>    findByCreatedBy(User doctor);
    Optional<Patient> findByNic(String nic);
    Boolean          existsByNic(String nic);
    List<Patient>    findByFullNameContainingIgnoreCase(String name);
}
