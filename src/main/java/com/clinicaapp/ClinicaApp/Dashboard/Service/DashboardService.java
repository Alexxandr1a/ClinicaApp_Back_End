package com.clinicaapp.ClinicaApp.Dashboard.Service;

import com.clinicaapp.ClinicaApp.Dashboard.DTO.DashboardDto;
import com.clinicaapp.ClinicaApp.Res.Response;

public interface DashboardService {
    Response<DashboardDto> getDashboard();
}

