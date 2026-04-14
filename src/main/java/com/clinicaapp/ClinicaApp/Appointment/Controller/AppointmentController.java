package com.clinicaapp.ClinicaApp.Appointment.Controller;

import com.clinicaapp.ClinicaApp.Appointment.DTO.AppointmentDTO;
import com.clinicaapp.ClinicaApp.Appointment.DTO.AppointmentStatusUpdateRequest;
import com.clinicaapp.ClinicaApp.Appointment.Service.AppointmentService;
import com.clinicaapp.ClinicaApp.Doctor.DTO.DoctorDto;
import com.clinicaapp.ClinicaApp.Doctor.Service.DoctorService;
import com.clinicaapp.ClinicaApp.Patient.DTO.PatientDto;
import com.clinicaapp.ClinicaApp.Patient.Service.PatientService;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<Response<AppointmentDTO>> bookAppointment(@RequestBody AppointmentDTO appointmentDTO){
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentDTO));
    }

    @GetMapping
    public  ResponseEntity<Response<List<AppointmentDTO>>> getMyAppointments(){
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

        @GetMapping("/search/patients")
    public ResponseEntity<Response<List<PatientDto>>> searchPatients(@RequestParam String name) {
        return ResponseEntity.ok(patientService.searchByName(name));
    }


    @GetMapping("/search/doctors")
    public ResponseEntity<Response<List<DoctorDto>>> searchDoctors(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.searchByName(name));
    }

    @PutMapping("/cancel/{appointmentId}")
    public  ResponseEntity<Response<AppointmentDTO>> cancelAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/complete/{appointmentId}")
    // @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public  ResponseEntity<Response<?>> completeAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }

    @PatchMapping("/{appointmentId}/status")
    public ResponseEntity<Response<AppointmentDTO>> updateStatusPatch(
            @PathVariable Long appointmentId,
            @RequestBody(required = false) AppointmentStatusUpdateRequest body) {
        String raw = body != null ? body.getStatus() : null;
        return applyStatusUpdate(appointmentId, raw);
    }

    /**
     * Aceita {@code ?status=CANCELADO} no query string (evita falha de deserialização JSON / proxy)
     * ou, se ausente, o corpo JSON {@link AppointmentStatusUpdateRequest}.
     */
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<Response<AppointmentDTO>> updateStatusPut(
            @PathVariable Long appointmentId,
            @RequestParam(name = "status", required = false) String statusParam,
            @RequestBody(required = false) AppointmentStatusUpdateRequest body) {
        String raw = (statusParam != null && !statusParam.isBlank())
                ? statusParam
                : (body != null ? body.getStatus() : null);
        return applyStatusUpdate(appointmentId, raw);
    }

    private ResponseEntity<Response<AppointmentDTO>> applyStatusUpdate(
            Long appointmentId,
            String statusRaw) {
        if (statusRaw == null || statusRaw.isBlank()) {
            return ResponseEntity.badRequest().body(Response.<AppointmentDTO>builder()
                    .success(false)
                    .statusCode(400)
                    .message("Campo \"status\" é obrigatório (query ?status=... ou JSON)")
                    .build());
        }
        final AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(statusRaw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Response.<AppointmentDTO>builder()
                    .success(false)
                    .statusCode(400)
                    .message("Status inválido: " + statusRaw)
                    .build());
        }
        return ResponseEntity.ok(appointmentService.updateStatus(appointmentId, status));
    }

}
