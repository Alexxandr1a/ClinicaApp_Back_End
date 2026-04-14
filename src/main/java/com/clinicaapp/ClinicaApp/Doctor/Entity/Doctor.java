package com.clinicaapp.ClinicaApp.Doctor.Entity;

import com.clinicaapp.ClinicaApp.Appointment.Entity.Appointment;
import com.clinicaapp.ClinicaApp.Enums.Specialization;
import com.clinicaapp.ClinicaApp.Users.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    // nullable=true para permitir migração automática (ddl-auto=update)
    // em bases já populadas; a validação pode ser feita na camada de request.
    @Column(unique = true)
    private String crm;

    private String phone;

    private String email;

    private String licenseNumber;

    private Integer consultation;

    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(length = 2000)
    private String biography;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}