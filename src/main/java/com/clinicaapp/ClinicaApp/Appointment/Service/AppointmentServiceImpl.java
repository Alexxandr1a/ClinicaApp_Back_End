package com.clinicaapp.ClinicaApp.Appointment.Service;

import com.clinicaapp.ClinicaApp.Appointment.DTO.AppointmentDTO;
import com.clinicaapp.ClinicaApp.Appointment.Entity.Appointment;
import com.clinicaapp.ClinicaApp.Appointment.Repo.AppointmentRepo;
import com.clinicaapp.ClinicaApp.Doctor.Entity.Doctor;
import com.clinicaapp.ClinicaApp.Doctor.Repo.DoctorRepo;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import com.clinicaapp.ClinicaApp.Patient.Entity.Patient;
import com.clinicaapp.ClinicaApp.Patient.Repo.PatientRepo;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO) {
        try {
            // Buscar paciente e médico pelo ID enviado no DTO
            Patient patient = patientRepo.findById(appointmentDTO.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            Doctor doctor = doctorRepo.findById(appointmentDTO.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            LocalDate date = appointmentDTO.getDate();
            LocalTime startTime = appointmentDTO.getStartTime();

            if (date.isBefore(LocalDate.now())) {
                throw new RuntimeException("Date cannot be in the past");
            }

            if (date.equals(LocalDate.now()) && startTime.isBefore(LocalTime.now())) {
                throw new RuntimeException("Time cannot be in the past");
            }

            if (startTime.isBefore(doctor.getStartTime()) || startTime.isAfter(doctor.getEndTime())) {
                throw new RuntimeException("Time is outside doctor's working hours");
            }

            LocalTime endTime = startTime.plusMinutes(30);

            // Checar conflitos
            List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                    doctor.getId(), date, startTime, endTime
            );

            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Doctor not available at this time");
            }

            // Regra nova: o paciente não pode ter mais de 1 consulta no mesmo dia
            List<Appointment> patientAppointments = appointmentRepo.findPatientAppointmentsOnDate(
                    patient.getId(),
                    date,
                    AppointmentStatus.CANCELADO
            );
            if (!patientAppointments.isEmpty()) {
                throw new RuntimeException("Paciente ja possui uma consulta neste dia");
            }

            // Criar agendamento
            Appointment appointment = Appointment.builder()
                    .date(date)
                    .startTime(startTime)
                    .endTime(endTime)
                    .status(appointmentDTO.getStatus() != null ? appointmentDTO.getStatus() : AppointmentStatus.PENDENTE)
                    .type(appointmentDTO.getType())
                    .doctor(doctor)
                    .patient(patient)
                    .build();

            Appointment saved = appointmentRepo.save(appointment);

            return Response.<AppointmentDTO>builder()
                    .success(true)
                    .message("Appointment created successfully")
                    .data(mapToDTO(saved))
                    .build();

        } catch (Exception e) {
            log.error("Erro ao criar agendamento: ", e);
            return Response.<AppointmentDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Response<List<AppointmentDTO>> getMyAppointments() {
        try {
            // Retorna todos os agendamentos sem checar usuário
            List<Appointment> appointments = appointmentRepo.findAll();

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(this::mapToDTO)
                    .toList();

            return Response.<List<AppointmentDTO>>builder()
                    .success(true)
                    .statusCode(200)
                    .message("Appointments retrieved")
                    .data(dtoList)
                    .build();
        } catch (Exception e) {
            return Response.<List<AppointmentDTO>>builder()
                    .statusCode(500)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public Response<AppointmentDTO> cancelAppointment(Long appointmentId) {
        try {
            Appointment appointment = appointmentRepo.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            appointment.setStatus(AppointmentStatus.CANCELADO);

            Appointment saved = appointmentRepo.save(appointment);

            return Response.<AppointmentDTO>builder()
                    .statusCode(200)
                    .message("Appointment cancelled")
                    .data(mapToDTO(saved))
                    .build();

        } catch (Exception e) {
            return Response.<AppointmentDTO>builder()
                    .statusCode(500)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public Response<?> completeAppointment(Long appointmentId) {
        try {
            Appointment appointment = appointmentRepo.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            appointment.setStatus(AppointmentStatus.COMPLETA);

            appointmentRepo.save(appointment);

            return Response.builder()
                    .statusCode(200)
                    .message("Appointment completed")
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .statusCode(500)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public Response<AppointmentDTO> updateStatus(Long appointmentId, AppointmentStatus status) {
        try {
            if (status == null) {
                throw new RuntimeException("Status is required");
            }
            int n = appointmentRepo.updateStatusById(appointmentId, status);
            if (n == 0) {
                throw new RuntimeException("Appointment not found");
            }

            Appointment appointment = appointmentRepo.findByIdWithDoctorAndPatient(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            return Response.<AppointmentDTO>builder()
                    .success(true)
                    .statusCode(200)
                    .message("Status updated")
                    .data(mapToDTO(appointment))
                    .build();
        } catch (Exception e) {
            log.error("Erro ao atualizar status: ", e);
            return Response.<AppointmentDTO>builder()
                    .success(false)
                    .statusCode(500)
                    .message(e.getMessage())
                    .build();
        }
    }

    private AppointmentDTO mapToDTO(Appointment a) {
        return AppointmentDTO.builder()
                .id(a.getId())
                .doctorId(a.getDoctor().getId())
                .patientId(a.getPatient().getId())
                .date(a.getDate())
                .patientFullName(a.getPatient().getFullName())
                .doctorFullName(a.getDoctor().getFullName())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .status(a.getStatus())
                .type(a.getType())
                .build();
    }
}