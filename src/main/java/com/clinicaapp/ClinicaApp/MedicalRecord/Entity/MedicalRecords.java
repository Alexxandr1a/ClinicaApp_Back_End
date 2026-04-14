package com.clinicaapp.ClinicaApp.MedicalRecord.Entity;


import com.clinicaapp.ClinicaApp.Attachment.Entity.Attachment;
import com.clinicaapp.ClinicaApp.Doctor.Entity.Doctor;
import com.clinicaapp.ClinicaApp.Enums.MedicalRecordsType;
import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "medical_records")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalRecordsType type;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(columnDefinition = "TEXT")
    private String evolutionObservations;

    @OneToMany(mappedBy = "medicalRecords", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;
}
