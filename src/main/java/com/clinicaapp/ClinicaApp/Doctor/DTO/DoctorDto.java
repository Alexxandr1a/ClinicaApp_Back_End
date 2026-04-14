package com.clinicaapp.ClinicaApp.Doctor.DTO;

import com.clinicaapp.ClinicaApp.Enums.Specialization;
import com.clinicaapp.ClinicaApp.Users.DTO.UserDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

    private Long id;

    private String fullName;
    private String crm;
    private String phone;
    private String email;

    private String licenseNumber;

    private Integer consultation;

    private Specialization specialization;

    private LocalTime startTime;
    private LocalTime endTime;

    private String biography;

    private UserDto userDto;


}