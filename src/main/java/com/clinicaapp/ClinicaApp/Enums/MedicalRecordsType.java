package com.clinicaapp.ClinicaApp.Enums;

public enum MedicalRecordsType {
    CONSULTA("Consulta"),
    RETORNO("Retorno"),
    EMERGENCIA("Emergencia");

    private final String description;

    MedicalRecordsType(String description){this.description = description;}
    public String getDescription(){return description;}
}
