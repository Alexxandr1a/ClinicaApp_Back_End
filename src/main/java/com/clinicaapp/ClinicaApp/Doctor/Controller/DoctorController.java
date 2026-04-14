package com.clinicaapp.ClinicaApp.Doctor.Controller;

import com.clinicaapp.ClinicaApp.Doctor.DTO.DoctorDto;
import com.clinicaapp.ClinicaApp.Doctor.Service.DoctorService;
import com.clinicaapp.ClinicaApp.Enums.Specialization;
import com.clinicaapp.ClinicaApp.Res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;


    @PostMapping
    public ResponseEntity<Response<DoctorDto>> createDoctor(@RequestBody @Valid DoctorDto doctorDto){
       return ResponseEntity.ok(doctorService.createDoctor(doctorDto));
    }

    @GetMapping("/me")
    public ResponseEntity<Response<DoctorDto>> getDoctorProfile() {
        return ResponseEntity.ok(doctorService.getDoctorProfile());
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:5173") // garante que o React pode chamar
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        Response<Void> response = doctorService.deleteDoctor(id);

        if (response.isSuccess()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 se não existir
        }
    }

    @PutMapping("/me")
    public ResponseEntity<Response<?>> updateDoctorProfile(@RequestBody DoctorDto doctorDTO) {
        return ResponseEntity.ok(doctorService.updateDoctorProfile(doctorDTO));
    }

    @GetMapping
    public ResponseEntity<Response<List<DoctorDto>>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<Response<DoctorDto>> getDoctorById(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @GetMapping("/filter")
    public ResponseEntity<Response<List<DoctorDto>>> searchDoctorsBySpecialization(
            @RequestParam Specialization specialization) {

        return ResponseEntity.ok(
                doctorService.searchDoctorsBySpecialization(specialization)
        );
    }

    @GetMapping("/specializations")
    public ResponseEntity<Response<List<Specialization>>> getAllSpecializationEnums() {
        return ResponseEntity.ok(
                doctorService.getAllSpecializationEnums()
        );
    }
}