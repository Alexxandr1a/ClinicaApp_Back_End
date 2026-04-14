package com.clinicaapp.ClinicaApp.Doctor.Service;

import com.clinicaapp.ClinicaApp.Doctor.DTO.DoctorDto;
import com.clinicaapp.ClinicaApp.Enums.Specialization;
import com.clinicaapp.ClinicaApp.Patient.DTO.PatientDto;
import com.clinicaapp.ClinicaApp.Res.Response;

import java.util.List;

public interface DoctorService {

    Response<DoctorDto> getDoctorProfile();

    Response<?> updateDoctorProfile(DoctorDto doctorDTO);

    Response<DoctorDto> createDoctor(DoctorDto doctorDto);

    Response<Void> deleteDoctor(Long doctorId);

    Response<List<DoctorDto>> getAllDoctors();

    Response<DoctorDto> getDoctorById(Long doctorId);

    Response<List<DoctorDto>> searchDoctorsBySpecialization(Specialization specialization);

    Response<List<Specialization>> getAllSpecializationEnums();

    Response<List<DoctorDto>> searchByName(String name);
}