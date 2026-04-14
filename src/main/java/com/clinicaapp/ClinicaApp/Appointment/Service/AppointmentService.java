package com.clinicaapp.ClinicaApp.Appointment.Service;

import com.clinicaapp.ClinicaApp.Appointment.DTO.AppointmentDTO;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import com.clinicaapp.ClinicaApp.Res.Response;

import java.util.List;

public interface AppointmentService {

    Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO);

    Response<List<AppointmentDTO>> getMyAppointments();

    Response<AppointmentDTO> cancelAppointment(Long appointmentId);

    Response<?> completeAppointment(Long appointmentId);

    Response<AppointmentDTO> updateStatus(Long appointmentId, AppointmentStatus status);
}
