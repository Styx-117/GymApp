package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.model.Usuario;
import com.smartfit.gymApp.repository.SocioDAO;
import com.smartfit.gymApp.repository.EntrenadorDAO;
import com.smartfit.gymApp.repository.UsuarioRepository;
import com.smartfit.gymApp.controller.ErroresDominio;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Falsos de repositorio: no abre Spring, conexiones ni peticiones externas. */
class DirectorioServiceTest {
    static class SociosFalsos extends SocioDAO {
        int escrituras;Socio recibido;
        public boolean registrarCliente(String n,String a,String d,String t,String c){escrituras++;return true;}
        public Socio guardarSocio(Socio s){escrituras++;recibido=s;return s;}
    }
    static class EntrenadoresFalsos extends EntrenadorDAO {
        int escrituras;
        public boolean registrarEntrenador(String n,String a,String e,String t){escrituras++;return true;}
    }
    static class UsuariosFalsos extends UsuarioRepository {
        String nombre,clave;int lecturas,escrituras;boolean activo=true;
        public Usuario autenticar(String n,String c){lecturas++;nombre=n;clave=c;return new Usuario(1L,n,null,null,activo);}
        public Usuario save(Usuario u){escrituras++;return u;}
    }
    @Test void swingYApiRechazanDniInvalidoAntesDeEscribir(){
        var dao=new SociosFalsos();var servicio=new SocioService(dao,null);
        assertThrows(IllegalArgumentException.class,()->servicio.registrarDatos("Ana","Quispe","123","987654321","ana@gamil.com"));
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(new Socio(null,"Ana","Quispe","123")));
        assertEquals(0,dao.escrituras);
    }
    @Test void reglasDeContactoSonComunesAFormularioYApi(){
        var dao=new SociosFalsos();var servicio=new SocioService(dao,null);
        assertThrows(IllegalArgumentException.class,()->servicio.registrarDatos("Ana","Quispe","12345678","numero","ana@gamil.com"));
        Socio api=new Socio(null,"Ana","Quispe","12345678");api.setTelefono("numero");
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(api));
        api.setTelefono("987654321");api.setCorreo("correo-invalido");
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(api));assertEquals(0,dao.escrituras);
    }
    @Test void apiConservaCamposCompletosYNormalizaContacto(){
        var dao=new SociosFalsos();var servicio=new SocioService(dao,null);
        Socio api=new Socio(null," Ana "," Quispe "," 12345678 ");api.setTelefono("987654321");api.setCorreo(" ana@gamil.com ");
        api.setFechaNacimiento(LocalDate.of(1995,2,3));api.setDireccion(" Ica ");api.setActivo(false);
        Socio guardado=servicio.guardar(api);
        assertSame(api,guardado);assertEquals("Ana",dao.recibido.getNombre());assertEquals("987654321",guardado.getTelefono());
        assertEquals("Ica",guardado.getDireccion());assertEquals(LocalDate.of(1995,2,3),guardado.getFechaNacimiento());assertFalse(guardado.getActivo());assertEquals(1,dao.escrituras);
    }
    @Test void dniYTelefonoRechazanSimbolosLetrasYDigitosNoAsciiAntesDePersistir(){
        var dao=new SociosFalsos();var servicio=new SocioService(dao,null);
        for(String dni:List.of("1234567A","1234-678","１２３４５６７８"))
            assertThrows(IllegalArgumentException.class,()->servicio.registrarDatos("Ana","Quispe",dni,"987654321",""));
        for(String telefono:List.of("+51987654321","987-654-321","987 654 321","(987)654321","98765432A","９８７６５４３２１")){
            assertThrows(IllegalArgumentException.class,()->servicio.registrarDatos("Ana","Quispe","12345678",telefono,""));
            Socio s=new Socio(null,"Ana","Quispe","12345678");s.setTelefono(telefono);
            assertThrows(IllegalArgumentException.class,()->servicio.guardar(s));
            assertThrows(IllegalArgumentException.class,()->ValidacionPersona.entrenador("Ana","Quispe","Yoga",telefono));
        }
        assertEquals(0,dao.escrituras);
    }
    @Test void fechaFuturaYDireccionExcesivaNoSePersisten(){
        var dao=new SociosFalsos();var servicio=new SocioService(dao,null);Socio s=new Socio(null,"Ana","Quispe","12345678");
        s.setFechaNacimiento(LocalDate.now().plusDays(1));assertThrows(IllegalArgumentException.class,()->servicio.guardar(s));
        s.setFechaNacimiento(null);s.setDireccion("x".repeat(201));assertThrows(IllegalArgumentException.class,()->servicio.guardar(s));assertEquals(0,dao.escrituras);
    }
    @Test void entrenadoresExigenEspecialidadYTelefonoValidoEnServicio(){
        var dao=new EntrenadoresFalsos();var servicio=new EntrenadorService(dao);
        assertThrows(IllegalArgumentException.class,()->servicio.registrar("Ana","Quispe"," ","987654321"));
        assertThrows(IllegalArgumentException.class,()->servicio.registrar("Ana","Quispe","Yoga","abc"));
        assertEquals(0,dao.escrituras);assertTrue(servicio.registrar("Ana","Quispe","Yoga","987654321"));assertEquals(1,dao.escrituras);
    }
    @Test void loginValidaNulosYConservaContrasenaLiteral(){
        var dao=new UsuariosFalsos();var servicio=new UsuarioService(dao);
        assertNull(servicio.login(null,"1234"));assertNull(servicio.login("admin",null));assertNull(servicio.login(" ","1234"));assertEquals(0,dao.lecturas);
        assertNotNull(servicio.login(" admin "," 1234 "));assertEquals("admin",dao.nombre);assertEquals(" 1234 ",dao.clave);
        dao.activo=false;assertNull(servicio.login("admin","1234"));
    }
    @Test void cuentasInvalidasNoLleganAlRepositorio(){
        var dao=new UsuariosFalsos();var servicio=new UsuarioService(dao);
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(new Usuario(null," ",null,"1234",true)));
        assertThrows(IllegalArgumentException.class,()->servicio.guardar(new Usuario(null,"admin",null," ",true)));assertEquals(0,dao.escrituras);
    }
    @Test void respuestaApiDeInfraestructuraNoExponeElDetalleSql(){
        var respuesta=new ErroresDominio().indisponible(new IllegalStateException("jdbc:password=secreto SQLSTATE=08001"));
        assertEquals(503,respuesta.getStatusCode().value());assertFalse(respuesta.getBody().toString().contains("secreto"));
        var invalida=new ErroresDominio().validacion(new IllegalArgumentException("DNI invalido"));assertEquals(400,invalida.getStatusCode().value());
        assertEquals("DNI invalido",((Map<?,?>)invalida.getBody()).get("error"));
    }
}
