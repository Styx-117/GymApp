package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Socio;
import java.time.LocalDate;

/** Reglas comunes a Swing, API y servicios; no depende de componentes graficos. */
public final class ValidacionPersona {
    private ValidacionPersona() {}
    public static String requerido(String valor, String campo, int maximo) {
        String texto=valor==null?"":valor.trim();
        if(texto.isEmpty()||texto.length()>maximo)throw new IllegalArgumentException(campo+" debe tener entre 1 y "+maximo+" caracteres.");
        return texto;
    }
    public static String dni(String valor) {
        String texto=valor==null?"":valor.trim();
        if(!texto.matches("[0-9]{8}"))throw new IllegalArgumentException("El DNI debe tener 8 digitos numericos.");
        return texto;
    }
    public static String telefono(String valor) {
        String texto=valor==null?"":valor.trim().replaceAll("[\\s()-]","");
        if(!texto.isEmpty()&&(texto.length()>15||!texto.matches("\\+?[0-9]{7,15}")))
            throw new IllegalArgumentException("El telefono debe contener entre 7 y 15 digitos, con prefijo + opcional.");
        return texto;
    }
    public static String correo(String valor) {
        String texto=valor==null?"":valor.trim();
        if(!texto.isEmpty()&&(texto.length()>100||!texto.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")))
            throw new IllegalArgumentException("Revisa el correo o deja el campo vacio.");
        return texto;
    }
    public static void socio(String nombre,String apellido,String dni,String telefono,String correo) {
        requerido(nombre,"El nombre",100);requerido(apellido,"El apellido",100);dni(dni);telefono(telefono);correo(correo);
    }
    public static void entrenador(String nombre,String apellido,String especialidad,String telefono) {
        requerido(nombre,"El nombre",100);requerido(apellido,"El apellido",100);requerido(especialidad,"La especialidad",100);telefono(telefono);
    }
    public static void normalizar(Socio socio) {
        if(socio==null)throw new IllegalArgumentException("Faltan los datos del socio.");
        socio.setNombre(requerido(socio.getNombre(),"El nombre",100));socio.setApellido(requerido(socio.getApellido(),"El apellido",100));
        socio.setDni(dni(socio.getDni()));socio.setTelefono(telefono(socio.getTelefono()));socio.setCorreo(correo(socio.getCorreo()));
        String direccion=socio.getDireccion()==null?null:socio.getDireccion().trim();
        if(direccion!=null&&direccion.length()>200)throw new IllegalArgumentException("La direccion admite hasta 200 caracteres.");
        if(socio.getFechaNacimiento()!=null&&socio.getFechaNacimiento().isAfter(LocalDate.now()))throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        if(socio.getIdSocio()!=null&&socio.getIdSocio()<=0)throw new IllegalArgumentException("El identificador debe ser positivo.");
        socio.setDireccion(direccion);
    }
}
