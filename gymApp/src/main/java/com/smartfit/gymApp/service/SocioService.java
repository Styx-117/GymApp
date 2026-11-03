package com.smartfit.gymApp.service;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.repository.SocioDAO;
import com.smartfit.gymApp.repository.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/** Mismas reglas y acceso JDBC para el directorio Swing y la API. */
@Service
public class SocioService {
    private final SocioDAO dao;
    private final ReniecService reniec;
    public SocioService(){this(new SocioDAO(),null);}
    @Autowired public SocioService(SocioDAO dao,ReniecService reniec){this.dao=dao;this.reniec=reniec;}
    public void validarDatos(String nombre,String apellido,String dni,String telefono,String correo){ValidacionPersona.socio(nombre,apellido,dni,telefono,correo);}
    public PageResult<SocioDAO.RegistroSocio> buscarPagina(String texto,Boolean activo,int pagina,int tamanio){return dao.buscarPagina(texto,activo,pagina,tamanio);}
    public List<Socio> listarTodos(){return dao.listarSocios();}
    public Socio guardar(Socio socio){ValidacionPersona.normalizar(socio);return dao.guardarSocio(socio);}
    public Socio buscarPorId(Long id){if(id==null||id<=0)throw new IllegalArgumentException("El identificador debe ser positivo.");return dao.buscarPorId(id);}
    public Socio buscarPorDni(String dni){return dao.buscarPorDni(ValidacionPersona.dni(dni));}
    public boolean existeDni(String dni){return buscarPorDni(dni)!=null;}
    public boolean eliminar(Long id){if(id==null||id<=0)throw new IllegalArgumentException("El identificador debe ser positivo.");return dao.cambiarEstado(Math.toIntExact(id),false);}
    public boolean cambiarEstado(int id,boolean activo){if(id<=0)throw new IllegalArgumentException("Selecciona un socio.");return dao.cambiarEstado(id,activo);}
    public boolean registrarDatos(String nombre,String apellido,String dni,String telefono,String correo){
        validarDatos(nombre,apellido,dni,telefono,correo);
        return dao.registrarCliente(nombre.trim(),apellido.trim(),dni.trim(),ValidacionPersona.telefono(telefono),ValidacionPersona.correo(correo));
    }
    public boolean actualizarDatos(int id,String nombre,String apellido,String dni,String telefono,String correo){
        if(id<=0)throw new IllegalArgumentException("Selecciona un socio.");validarDatos(nombre,apellido,dni,telefono,correo);
        // Solo cambia los campos editables de Swing: conserva codigo, fechas, direccion y estado.
        return dao.actualizarCliente(id,nombre.trim(),apellido.trim(),dni.trim(),ValidacionPersona.telefono(telefono),ValidacionPersona.correo(correo));
    }
    private static class ClienteLocal {private static final ReniecApiClient CLIENTE=new ReniecApiClient();}
    public ReniecData consultarDni(String dni){
        String numero=ValidacionPersona.dni(dni);
        if(reniec!=null)return reniec.consultarDni(numero);
        try{return ClienteLocal.CLIENTE.consultarDni(numero);}
        catch(InterruptedException ex){Thread.currentThread().interrupt();throw new IllegalStateException("La consulta de DNI fue cancelada.",ex);}
        catch(java.io.IOException ex){throw new IllegalStateException("La consulta de DNI no esta disponible.",ex);}
    }
    public Socio registrarPorDni(String dni){
        String numero=ValidacionPersona.dni(dni);Socio existente=dao.buscarPorDni(numero);if(existente!=null)return existente;
        ReniecData data=consultarDni(numero);Socio socio=new Socio();socio.setDni(numero);socio.setNombre(data.getNombres());
        socio.setApellido(java.util.stream.Stream.of(data.getApellidoPaterno(),data.getApellidoMaterno()).filter(s->s!=null&&!s.isBlank()).collect(java.util.stream.Collectors.joining(" ")));
        socio.setActivo(true);return guardar(socio);
    }
}
