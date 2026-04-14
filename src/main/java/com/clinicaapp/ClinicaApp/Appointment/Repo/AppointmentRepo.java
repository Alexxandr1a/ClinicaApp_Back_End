package com.clinicaapp.ClinicaApp.Appointment.Repo;

import com.clinicaapp.ClinicaApp.Appointment.Entity.Appointment;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.id = :id")
    Optional<Appointment> findByIdWithDoctorAndPatient(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") AppointmentStatus status);

    List<Appointment> findByDoctor_User_IdOrderByIdDesc(Long userId);

    List<Appointment> findByPatient_User_IdOrderByIdDesc(Long userId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.date = :date " +
            "AND a.status != 'CANCELADO' " +
            "AND (a.startTime < :endTime AND a.endTime > :startTime)")
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.date = :date " +
            "AND a.status <> :cancelStatus")
    List<Appointment> findPatientAppointmentsOnDate(
            @Param("patientId") Long patientId,
            @Param("date") LocalDate date,
            @Param("cancelStatus") AppointmentStatus cancelStatus
    );

    @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor d JOIN FETCH a.patient p WHERE a.date = :date")
    List<Appointment> findByDateWithDoctorAndPatient(@Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor d JOIN FETCH a.patient p WHERE a.date BETWEEN :start AND :end")
    List<Appointment> findByDateBetweenWithDoctorAndPatient(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
