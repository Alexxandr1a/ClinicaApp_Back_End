package com.clinicaapp.ClinicaApp.Exeption;

public class BadRequestExeption extends RuntimeException{

    public BadRequestExeption(String ex){
        super(ex);
    }
}
