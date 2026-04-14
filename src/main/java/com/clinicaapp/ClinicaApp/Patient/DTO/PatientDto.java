package com.clinicaapp.ClinicaApp.Patient.DTO;

import com.clinicaapp.ClinicaApp.Enums.BloodGroup;
import com.clinicaapp.ClinicaApp.Enums.Genotype;
import com.clinicaapp.ClinicaApp.Users.DTO.UserDto;
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
public class PatientDto {

    private Long id;

    // Dados Básicos
    private String fullName;
    private String cpf;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;

    // Dados médicos
    private String allergies;
    private String agreement;
    private String conditions;
    private String ongoingMedications;
    private String insurance;
    private String medications;
    private BloodGroup bloodGroup;
    private Genotype genotype;

    // Observações
    private String observations;

    private UserDto userDto;
}