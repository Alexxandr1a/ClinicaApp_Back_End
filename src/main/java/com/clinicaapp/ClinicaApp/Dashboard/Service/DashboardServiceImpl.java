package com.clinicaapp.ClinicaApp.Dashboard.Service;

import com.clinicaapp.ClinicaApp.Appointment.Entity.Appointment;
import com.clinicaapp.ClinicaApp.Appointment.Repo.AppointmentRepo;
import com.clinicaapp.ClinicaApp.Dashboard.DTO.DashboardDto;
import com.clinicaapp.ClinicaApp.Enums.AppointmentStatus;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AppointmentRepo appointmentRepo;

    @Override
    @Transactional(readOnly = true)
    public Response<DashboardDto> getDashboard() {
        try {
            LocalDate today = LocalDate.now();

            List<Appointment> todayAppointments = appointmentRepo.findByDateWithDoctorAndPatient(today);

            long consultasHoje = todayAppointments.size();

            long pendentes = todayAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.PENDENTE)
                    .count();

            long canceladas = todayAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.CANCELADO)
                    .count();

            long confirmadas = todayAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMADO)
                    .count();

            long medicosAtendendo = todayAppointments.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.CANCELADO)
                    .map(a -> a.getDoctor().getId())
                    .distinct()
                    .count();

            long pacientesAgendados = todayAppointments.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.CANCELADO)
                    .map(a -> a.getPatient().getId())
                    .distinct()
                    .count();

            long concluidas = todayAppointments.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.COMPLETA    )
                    .map(a -> a.getPatient().getId())
                    .distinct()
                    .count();

            DashboardDto.Stats stats = DashboardDto.Stats.builder()
                    .consultasHoje(consultasHoje)
                    .medicosAtendendo(medicosAtendendo)
                    .pacientesAgendados(pacientesAgendados)
                    .pendentes(pendentes)
                    .canceladas(canceladas)
                    .build();

            // Últimos 4 meses (incluindo o atual) para o gráfico
            YearMonth current = YearMonth.now();
            Map<YearMonth, Long> monthCounts = new LinkedHashMap<>();
            for (int i = 3; i >= 0; i--) {
                YearMonth ym = current.minusMonths(i);
                monthCounts.put(ym, 0L);
            }
            LocalDate start = monthCounts.keySet().iterator().next().atDay(1);
            LocalDate end = current.atEndOfMonth();

            List<Appointment> rangeAppointments = appointmentRepo.findByDateBetweenWithDoctorAndPatient(start, end);

            Map<YearMonth, Long> computed = rangeAppointments.stream()
                    .collect(Collectors.groupingBy(a -> YearMonth.from(a.getDate()), Collectors.counting()));

            computed.forEach((ym, count) -> {
                if (monthCounts.containsKey(ym)) monthCounts.put(ym, count);
            });

            List<DashboardDto.MonthlyPoint> monthly = monthCounts.entrySet().stream()
                    .map(e -> DashboardDto.MonthlyPoint.builder()
                            .month(e.getKey().getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")))
                            .consultas(e.getValue())
                            .build())
                    .toList();

            List<DashboardDto.StatusPoint> todayStatus = List.of(
                    DashboardDto.StatusPoint.builder().name("Confirmadas").value(confirmadas).color("#22c55e").build(),
                    DashboardDto.StatusPoint.builder().name("Pendentes").value(pendentes).color("#f59e0b").build(),
                    DashboardDto.StatusPoint.builder().name("Canceladas").value(canceladas).color("#ef4444").build(),
                    DashboardDto.StatusPoint.builder().name("Concluídas").value(canceladas).color("#91a1e2").build()
            );

            List<DashboardDto.TodayAppointmentRow> rows = todayAppointments.stream()
                    .sorted(Comparator.comparing(Appointment::getStartTime))
                    .limit(10) // dashboard preview
                    .map(a -> DashboardDto.TodayAppointmentRow.builder()
                            .appointmentId(a.getId())
                            .time(a.getStartTime() != null ? a.getStartTime().toString().substring(0, 5) : "")
                            .patient(a.getPatient() != null ? a.getPatient().getFullName() : "")
                            .doctor(a.getDoctor() != null ? a.getDoctor().getFullName() : "")
                            .specialty(a.getDoctor() != null && a.getDoctor().getSpecialization() != null ? a.getDoctor().getSpecialization().name() : "")
                            .status(a.getStatus() != null ? a.getStatus().name() : "")
                            .type(a.getType() != null ? a.getType().name() : "")
                            .build())
                    .toList();

            DashboardDto dto = DashboardDto.builder()
                    .stats(stats)
                    .monthlyConsultations(monthly)
                    .todayStatus(todayStatus)
                    .todayAppointments(rows)
                    .build();

            return Response.<DashboardDto>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .data(dto)
                    .build();
        } catch (Exception e) {
            return Response.<DashboardDto>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro ao carregar dashboard: " + e.getMessage())
                    .build();
        }
    }
}

