package com.clinicaapp.ClinicaApp.Patient.Controller;

import com.clinicaapp.ClinicaApp.Enums.BloodGroup;
import com.clinicaapp.ClinicaApp.Enums.Genotype;
import com.clinicaapp.ClinicaApp.Patient.DTO.PatientDto;
import com.clinicaapp.ClinicaApp.Patient.Service.PatientService;
import com.clinicaapp.ClinicaApp.Res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;

    // ✅ CADASTRAR PACIENTE
    @PostMapping
    public ResponseEntity<Response<PatientDto>> createPatient(
            @RequestBody @Valid PatientDto patientDto) {

        return ResponseEntity.ok(patientService.createPatient(patientDto));
    }

    // ✅ 🔥 LISTAR TODOS OS PACIENTES (FALTAVA ISSO)
    @GetMapping
    public ResponseEntity<Response<List<PatientDto>>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // ✅ PEGAR PERFIL DO PACIENTE LOGADO
    @GetMapping("/me")
    public ResponseEntity<Response<PatientDto>> getPatientProfile() {
        return ResponseEntity.ok(patientService.getPatientProfile());
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:5173") // garante que o React pode chamar
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        Response<Void> response = patientService.deletePatient(id);

        if (response.isSuccess()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 se não existir
        }
    }

    // ✅ BUSCAR POR ID
    @GetMapping("/{patientId}")
    public ResponseEntity<Response<PatientDto>> getPatientById(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    // ✅ ENUM BLOOD GROUP
    @GetMapping("/bloodgroup")
    public ResponseEntity<Response<List<BloodGroup>>> getAllBloodGroupEnums() {
        return ResponseEntity.ok(patientService.getAllBloodGroupEnums());
    }

    // ✅ ENUM GENOTYPE
    @GetMapping("/genotype")
    public ResponseEntity<Response<List<Genotype>>> getAllGenotypeEnums() {
        return ResponseEntity.ok(patientService.getAllGenotypeEnums());
    }


}