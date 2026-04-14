package com.clinicaapp.ClinicaApp.Patient.Service;

import com.clinicaapp.ClinicaApp.Enums.BloodGroup;
import com.clinicaapp.ClinicaApp.Enums.Genotype;
import com.clinicaapp.ClinicaApp.Patient.DTO.PatientDto;
import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import com.clinicaapp.ClinicaApp.Patient.Repo.PatientRepo;
import com.clinicaapp.ClinicaApp.Res.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepo patientRepo;
    private final ModelMapper modelMapper;


    @Override
    public Response<PatientDto> createPatient(PatientDto patientDto) {

        try {

            Patient patient = modelMapper.map(patientDto, Patient.class);

            Patient saved = patientRepo.save(patient);

            PatientDto response = modelMapper.map(saved, PatientDto.class);

            return Response.<PatientDto>builder()
                    .success(true)
                    .message("Patient created successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {

            return Response.<PatientDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public Response<PatientDto> getPatientProfile() {
        return Response.<PatientDto>builder()
                .success(true)
                .message("Profile loaded")
                .data(null)
                .build();
    }

    @Override
    public Response<?> updatePatientProfile(PatientDto patientDTO) {
        return Response.builder()
                .success(true)
                .message("Profile updated")
                .build();
    }

    @Override
    public Response<PatientDto> getPatientById(Long patientId) {
        return Response.<PatientDto>builder()
                .success(true)
                .message("Patient found")
                .data(null)
                .build();
    }

    @Override
    public Response<List<BloodGroup>> getAllBloodGroupEnums() {
        return Response.<List<BloodGroup>>builder()
                .success(true)
                .message("Blood groups")
                .data(Arrays.asList(BloodGroup.values()))
                .build();
    }

    @Override
    public Response<List<Genotype>> getAllGenotypeEnums() {
        return Response.<List<Genotype>>builder()
                .success(true)
                .message("Genotypes")
                .data(Arrays.asList(Genotype.values()))
                .build();
    }

    @Override
    public Response<List<PatientDto>> getAllPatients() {
        try {

            List<Patient> patients = patientRepo.findAll();

            List<PatientDto> patientsDto = patients.stream()
                    .map(p -> modelMapper.map(p, PatientDto.class))
                    .toList();

            return Response.<List<PatientDto>>builder()
                    .success(true)
                    .message("Patients retrieved successfully")
                    .data(patientsDto)
                    .build();

        } catch (Exception e) {

            return Response.<List<PatientDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
        }
    @Override
    @Transactional
    public Response<Void> deletePatient(Long patientId) {
        try {
            if (!patientRepo.existsById(patientId)) {
                return Response.<Void>builder()
                        .success(false)
                        .message("Patient with ID " + patientId + " not found")
                        .build();
            }

            patientRepo.deleteById(patientId);

            return Response.<Void>builder()
                    .success(true)
                    .message("Patient deleted successfully")
                    .build();

        } catch (Exception e) {
            return Response.<Void>builder()
                    .success(false)
                    .message("Error deleting patient: " + e.getMessage())
                    .build();
        }
    }
    @Override
    public Response<List<PatientDto>> searchByName(String name) {

        List<Object[]> results = patientRepo.searchBasicByName(name);

        List<PatientDto> dtos = results.stream()
                .map(r -> PatientDto.builder()
                        .id((Long) r[0])
                        .fullName((String) r[1])
                        .build()
                )
                .toList();

        return Response.<List<PatientDto>>builder()
                .success(true)
                .data(dtos)
                .build();
    }
    }