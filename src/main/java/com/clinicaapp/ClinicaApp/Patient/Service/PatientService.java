package com.clinicaapp.ClinicaApp.Patient.Service;

import com.clinicaapp.ClinicaApp.Enums.BloodGroup;
import com.clinicaapp.ClinicaApp.Enums.Genotype;
import com.clinicaapp.ClinicaApp.Patient.DTO.PatientDto;
import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import com.clinicaapp.ClinicaApp.Res.Response;

import java.util.List;

public interface PatientService {

    Response<PatientDto> getPatientProfile();

    Response<PatientDto> createPatient(PatientDto patientDto);

    Response<?> updatePatientProfile(PatientDto patientDTO);

    Response<PatientDto> getPatientById(Long patientId);

    Response<List<BloodGroup>> getAllBloodGroupEnums();

    Response<List<Genotype>> getAllGenotypeEnums();

    Response<List<PatientDto>> getAllPatients();

    Response<Void> deletePatient(Long patientId);

    Response<List<PatientDto>> searchByName(String name);




}