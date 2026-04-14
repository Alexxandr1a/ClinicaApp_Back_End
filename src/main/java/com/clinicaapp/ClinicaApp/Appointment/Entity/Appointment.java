package com.clinicaapp.ClinicaApp.Appointment.Entity;

import com.clinicaapp.ClinicaApp.Doctor.Entity.Doctor;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import com.clinicaapp.ClinicaApp.Enums.AppointmentType;
import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(nullable = false)
    private LocalDate date;

    private String meetingLink; //reuniao

// private String purposeOfConsultation;

     private String initialSymptoms;//sintomas iniciais

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    private AppointmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;



}
