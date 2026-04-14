package com.clinicaapp.ClinicaApp.Dashboard.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private Stats stats;
    private List<MonthlyPoint> monthlyConsultations;
    private List<StatusPoint> todayStatus;
    private List<TodayAppointmentRow> todayAppointments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private long consultasHoje;
        private long medicosAtendendo;
        private long pacientesAgendados;
        private long pendentes;
        private long canceladas;
        private long concluidas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyPoint {
        private String month;     // ex: "Jan"
        private long consultas;   // quantidade
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusPoint {
        private String name;   // Confirmadas/Pendentes/Canceladas
        private long value;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayAppointmentRow {
        private String time;          // "08:30"
        private String patient;       // nome
        private String doctor;        // nome
        private String specialty;     // especialidade
        private String status;        // enum
        private String type;          // enum
        private Long appointmentId;
    }
}

