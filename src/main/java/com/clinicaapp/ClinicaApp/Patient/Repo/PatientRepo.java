package com.clinicaapp.ClinicaApp.Patient.Repo;

import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicaapp.ClinicaApp.Users.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    @Query("SELECT p.id, p.fullName FROM Patient p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Object[]> searchBasicByName(@Param("name") String name);
}
