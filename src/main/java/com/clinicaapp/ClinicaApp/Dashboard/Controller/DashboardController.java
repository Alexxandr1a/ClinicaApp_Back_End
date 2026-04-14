package com.clinicaapp.ClinicaApp.Dashboard.Controller;

import com.clinicaapp.ClinicaApp.Dashboard.DTO.DashboardDto;
import com.clinicaapp.ClinicaApp.Dashboard.Service.DashboardService;
import com.clinicaapp.ClinicaApp.Res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Response<DashboardDto>> getDashboard() {
        Response<DashboardDto> response = dashboardService.getDashboard();
        int status = response.getStatusCode() > 0 ? response.getStatusCode() : 200;
        return ResponseEntity.status(status).body(response);
    }
}

