package com.smartfit.gymApp.dto;

public class ReniecRequest {
    private String dni;

    public ReniecRequest() {}
    public ReniecRequest(String dni) { this.dni = dni; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
}
