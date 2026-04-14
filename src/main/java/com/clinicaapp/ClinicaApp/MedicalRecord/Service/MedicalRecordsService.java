package com.clinicaapp.ClinicaApp.MedicalRecord.Service;

import com.clinicaapp.ClinicaApp.Doctor.Repo.DoctorRepo;
import com.clinicaapp.ClinicaApp.MedicalRecord.Entity.MedicalRecords;
import com.clinicaapp.ClinicaApp.MedicalRecord.DTO.MedicalRecordsDto;
import com.clinicaapp.ClinicaApp.MedicalRecord.Repo.MedicalRecordsRepo;
import com.clinicaapp.ClinicaApp.Patient.Repo.PatientRepo;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordsService {

    private final MedicalRecordsRepo medicalRecordsRepo;
    private final DoctorRepo doctorRepo;
    private final PatientRepo patientRepo;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Response<Page<MedicalRecordsDto>> listar(String search, Pageable pageable) {
        try {
            Page<MedicalRecords> pagina = (search != null && !search.isBlank())
                    ? medicalRecordsRepo.searchByPatientOrDiagnosis(search, pageable)
                    : medicalRecordsRepo.findAll(pageable);

            Page<MedicalRecordsDto> mapped = pagina.map(this::toDto);

            return Response.<Page<MedicalRecordsDto>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .data(mapped)
                    .build();
        } catch (Exception e) {
            return Response.<Page<MedicalRecordsDto>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro ao listar prontuários: " + e.getMessage())
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public Response<MedicalRecordsDto> buscarPorId(Long id) {
        try {
            MedicalRecords mr = medicalRecordsRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Medical record not found: " + id));

            return Response.<MedicalRecordsDto>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .data(toDto(mr))
                    .build();
        } catch (Exception e) {
            return Response.<MedicalRecordsDto>builder()
                    .success(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<MedicalRecordsDto> create(MedicalRecordsDto dto) {
        try {
            var doctor = doctorRepo.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found: " + dto.getDoctorId()));

            var patient = patientRepo.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));

            MedicalRecords mr = MedicalRecords.builder()
                    .patient(patient)
                    .doctor(doctor)
                    .date(dto.getDate())
                    .type(dto.getType())
                    .symptoms(dto.getSymptoms())
                    .diagnosis(dto.getDiagnosis())
                    .prescription(dto.getPrescription())
                    .evolutionObservations(dto.getEvolutionObservations())
                    .build();

            MedicalRecords saved = medicalRecordsRepo.save(mr);

            return Response.<MedicalRecordsDto>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Medical record created")
                    .data(toDto(saved))
                    .build();
        } catch (Exception e) {
            return Response.<MedicalRecordsDto>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Erro ao criar prontuário: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<MedicalRecordsDto> update(Long id, MedicalRecordsDto dto) {
        try {
            MedicalRecords mr = medicalRecordsRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Medical record not found: " + id));

            var doctor = doctorRepo.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found: " + dto.getDoctorId()));

            var patient = patientRepo.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));

            mr.setPatient(patient);
            mr.setDoctor(doctor);
            mr.setDate(dto.getDate());
            mr.setType(dto.getType());
            mr.setSymptoms(dto.getSymptoms());
            mr.setDiagnosis(dto.getDiagnosis());
            mr.setPrescription(dto.getPrescription());
            mr.setEvolutionObservations(dto.getEvolutionObservations());

            MedicalRecords saved = medicalRecordsRepo.save(mr);

            return Response.<MedicalRecordsDto>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Medical record updated")
                    .data(toDto(saved))
                    .build();
        } catch (Exception e) {
            return Response.<MedicalRecordsDto>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Erro ao atualizar prontuário: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<Void> excluir(Long id) {
        try {
            if (!medicalRecordsRepo.existsById(id)) {
                return Response.<Void>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Medical record not found: " + id)
                        .build();
            }

            medicalRecordsRepo.deleteById(id);

            return Response.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .message("Medical record deleted")
                    .build();
        } catch (Exception e) {
            return Response.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro ao excluir prontuário: " + e.getMessage())
                    .build();
        }
    }

    private MedicalRecordsDto toDto(MedicalRecords mr) {
        MedicalRecordsDto dto = modelMapper.map(mr, MedicalRecordsDto.class);
        dto.setPatientId(mr.getPatient() != null ? mr.getPatient().getId() : null);
        dto.setPatientFullName(mr.getPatient() != null ? mr.getPatient().getFullName() : null);
        dto.setDoctorId(mr.getDoctor() != null ? mr.getDoctor().getId() : null);
        dto.setDoctorFullName(mr.getDoctor() != null ? mr.getDoctor().getFullName() : null);
        return dto;
    }
}