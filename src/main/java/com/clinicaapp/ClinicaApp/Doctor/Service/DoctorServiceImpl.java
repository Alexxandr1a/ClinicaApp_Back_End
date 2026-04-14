package com.clinicaapp.ClinicaApp.Doctor.Service;

import com.clinicaapp.ClinicaApp.Doctor.DTO.DoctorDto;
import com.clinicaapp.ClinicaApp.Doctor.Entity.Doctor;
import com.clinicaapp.ClinicaApp.Doctor.Repo.DoctorRepo;
import com.clinicaapp.ClinicaApp.Enums.Specialization;
import com.clinicaapp.ClinicaApp.Res.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepo doctorRepo;
    private final ModelMapper modelMapper;

    @Override
    public Response<DoctorDto> createDoctor(DoctorDto doctorDto) {

        try {

            Doctor doctor = new Doctor();

            doctor.setFullName(doctorDto.getFullName());
            doctor.setPhone(doctorDto.getPhone());
            doctor.setEmail(doctorDto.getEmail());
            doctor.setLicenseNumber(doctorDto.getLicenseNumber());

            doctor.setSpecialization(doctorDto.getSpecialization());

            doctor.setStartTime(doctorDto.getStartTime());
            doctor.setEndTime(doctorDto.getEndTime());

            doctor.setBiography(doctorDto.getBiography());

            // 🔥 evita null que pode quebrar
            doctor.setConsultation(0);

            Doctor saved = doctorRepo.save(doctor);

            DoctorDto response = modelMapper.map(saved, DoctorDto.class);

            return Response.<DoctorDto>builder()
                    .success(true)
                    .message("Doctor created successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {

            e.printStackTrace(); // 🔥 MOSTRA O ERRO REAL NO CONSOLE

            return Response.<DoctorDto>builder()
                    .success(false)
                    .message("Erro ao salvar: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response<DoctorDto> getDoctorProfile() {

        return Response.<DoctorDto>builder()
                .success(true)
                .message("Doctor profile")
                .build();
    }

    @Override
    public Response<?> updateDoctorProfile(DoctorDto doctorDTO) {

        return Response.builder()
                .success(true)
                .message("Doctor updated")
                .build();
    }

    @Override
    public Response<List<DoctorDto>> getAllDoctors() {

        List<DoctorDto> doctors = doctorRepo.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class))
                .collect(Collectors.toList());

        return Response.<List<DoctorDto>>builder()
                .success(true)
                .data(doctors)
                .build();
    }

    @Override
    public Response<DoctorDto> getDoctorById(Long doctorId) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorDto dto = modelMapper.map(doctor, DoctorDto.class);

        return Response.<DoctorDto>builder()
                .success(true)
                .data(dto)
                .build();
    }

    @Override
    public Response<List<DoctorDto>> searchDoctorsBySpecialization(Specialization specialization) {

        List<Doctor> doctors = doctorRepo.findBySpecialization(specialization);

        List<DoctorDto> dtoList = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class))
                .collect(Collectors.toList());

        return Response.<List<DoctorDto>>builder()
                .success(true)
                .data(dtoList)
                .build();
    }

    @Override
    public Response<List<Specialization>> getAllSpecializationEnums() {

        return Response.<List<Specialization>>builder()
                .success(true)
                .data(List.of(Specialization.values()))
                .build();
    }

    @Override
    @Transactional
    public Response<Void> deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepo.existsById(doctorId)) {
                return Response.<Void>builder()
                        .success(false)
                        .message("Doctor with ID " + doctorId + " not found")
                        .build();
            }

            doctorRepo.deleteById(doctorId);

            return Response.<Void>builder()
                    .success(true)
                    .message("Doctor deleted successfully")
                    .build();

        } catch (Exception e) {
            return Response.<Void>builder()
                    .success(false)
                    .message("Error deleting doctor: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response<List<DoctorDto>> searchByName(String name) {

        List<DoctorDto> doctors = doctorRepo
                .findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(d -> {
                    DoctorDto dto = new DoctorDto();
                    dto.setId(d.getId());
                    dto.setFullName(d.getFullName());
                    dto.setStartTime(d.getStartTime());
                    dto.setEndTime(d.getEndTime());
                    return dto;
                })
                .toList();

        return Response.<List<DoctorDto>>builder()
                .data(doctors)
                .message("Médicos encontrados")
                .build();
    }
}