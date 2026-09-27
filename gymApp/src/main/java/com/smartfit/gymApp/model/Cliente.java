package com.smartfit.gymApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank
    @Size(min = 8, max = 8)
    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public Cliente() {}

    public Cliente(String nombre, String apellido, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    public Long getIdCliente() {
         return idCliente; 
    }

    public void setIdCliente(Long idCliente) { 
        this.idCliente = idCliente; 
    }

    public String getNombre() { 
        return nombre; 
    }

    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getApellido() { 
        return apellido; 
    }

    public void setApellido(String apellido) { 
        this.apellido = apellido; 
    }

    public String getDni() { 
        return dni;
    }

    public void setDni(String dni) { 
        this.dni = dni;
    }

    public String getTelefono() { 
        return telefono; 
    }

    public void setTelefono(String telefono) { 
        this.telefono = telefono; 
    }

    public String getCorreo() { 
        return correo; 
    }

    public void setCorreo(String correo) { 
        this.correo = correo;
    }

    public LocalDateTime getFechaRegistro() { 
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) { 
        this.fechaRegistro = fechaRegistro; 
    }
}