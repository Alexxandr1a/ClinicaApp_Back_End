package com.clinicaapp.ClinicaApp.Appointment.DTO;


import com.clinicaapp.ClinicaApp.Consultation.Entity.Consultation;
import com.clinicaapp.ClinicaApp.Doctor.DTO.DoctorDto;
import com.clinicaapp.ClinicaApp.Doctor.Entity.Doctor;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import com.clinicaapp.ClinicaApp.Enums.AppointmentType;
import com.clinicaapp.ClinicaApp.Patient.DTO.PatientDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDTO {

    private Long id;
    private String patientFullName;
    private String doctorFullName;

    @NotNull
    private Long doctorId;

    @NotNull
    private Long patientId;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;

    private LocalTime endTime;

    private AppointmentStatus status;

    private AppointmentType type;
}
