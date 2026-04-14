package com.clinicaapp.ClinicaApp.MedicalRecord.Repo;

import com.clinicaapp.ClinicaApp.Enums.MedicalRecordsType;
import com.clinicaapp.ClinicaApp.MedicalRecord.Entity.MedicalRecords;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordsRepo extends JpaRepository<MedicalRecords, Long> {
    @Query("""
            SELECT mr FROM MedicalRecords mr
            WHERE LOWER(mr.patient.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))
            """)
    Page<MedicalRecords> findByPatientFullNameContainingIgnoreCase(
            @Param("fullName") String fullName,
            Pageable pageable
    );

    @Query("""
            SELECT mr FROM MedicalRecords mr
            WHERE LOWER(mr.patient.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<MedicalRecords> searchByPatientOrDiagnosis(
            @Param("search") String search,
            Pageable pageable
    );

    List<MedicalRecords> findByDoctorId(Long doctorId);
    Page<MedicalRecords> findByType(MedicalRecordsType type, Pageable pageable);
}
