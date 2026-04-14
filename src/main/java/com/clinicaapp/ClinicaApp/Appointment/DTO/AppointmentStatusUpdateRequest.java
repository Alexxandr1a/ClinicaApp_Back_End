package com.clinicaapp.ClinicaApp.Appointment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Usa {@code String} para o Jackson não falhar em edge cases com enum; o controller faz o parse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusUpdateRequest {

    private String status;
}
