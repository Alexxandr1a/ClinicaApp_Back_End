package com.clinicaapp.ClinicaApp.MedicalRecord.DTO;

import com.clinicaapp.ClinicaApp.Enums.MedicalRecordsType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicalRecordsDto {

    private Long id;

    private Long patientId;
    private String patientFullName;

    private Long doctorId;
    private String doctorFullName;

    private LocalDate date;
    private MedicalRecordsType type;

    private String symptoms;
    private String diagnosis;
    private String prescription;
    private String evolutionObservations;
}
