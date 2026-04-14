package com.clinicaapp.ClinicaApp.Patient.Entity;

import com.clinicaapp.ClinicaApp.Appointment.Entity.Appointment;
import com.clinicaapp.ClinicaApp.Enums.BloodGroup;
import com.clinicaapp.ClinicaApp.Enums.Genotype;
import com.clinicaapp.ClinicaApp.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dados Básicos
    private String fullName;
    private String cpf;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;

    // Dados médicos (TEXT em PostgreSQL evita LOB stream / sessão fechada)
    @Column(columnDefinition = "text")
    private String allergies;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String insurance;

    private String medications;

    @Enumerated(EnumType.STRING)
    private Genotype genotype;

    private String agreement;

    @Column(columnDefinition = "text")
    private String conditions;

    @Column(columnDefinition = "text")
    private String ongoingMedications;

    // Observação

    private String observations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}