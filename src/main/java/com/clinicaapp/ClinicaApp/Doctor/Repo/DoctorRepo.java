package com.clinicaapp.ClinicaApp.Doctor.Repo;

import com.clinicaapp.ClinicaApp.Doctor.Entity.Doctor;
import com.clinicaapp.ClinicaApp.Enums.Specialization;
import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(User user);
    List<Doctor> findByFullNameContainingIgnoreCase(String name);
    List<Doctor> findBySpecialization(Specialization specialization);
    Optional<Doctor> findByCrm(String crm);
}
