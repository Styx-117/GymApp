package com.smartfit.gymApp.service;

import com.smartfit.gymApp.model.Socio;
import com.smartfit.gymApp.repository.PageResult;
import com.smartfit.gymApp.repository.SocioDAO;
import com.smartfit.gymApp.structures.arreglos.ArregloSocios;
import com.smartfit.gymApp.structures.avl.ArbolAVL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/** Instantánea de una consulta: la BD persiste; el arreglo y AVL resuelven las consultas. */
public final class DirectorioSocios {
    private final ArregloSocios socios = new ArregloSocios(32);
    private final ArbolAVL<Socio> indice = new ArbolAVL<>();
    public DirectorioSocios(Iterable<Socio> registros) {
        indice.setEtiquetador(Socio::getCodigoSocio);
        for (Socio socio : registros) { socios.agregar(socio); indice.insertar(socio); }
    }
    public ArbolAVL<Socio> indice() { return indice; }
    public Socio buscarCodigo(String codigo) {
        return indice.buscar(new Socio(codigo.trim().toUpperCase(Locale.ROOT), "", "", ""));
    }
    public PageResult<SocioDAO.RegistroSocio> pagina(String texto, Boolean activo, int pagina, int cantidad) {
        if (pagina < 0 || cantidad < 1 || cantidad > 200) throw new IllegalArgumentException("Página inválida.");
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        ArregloSocios seleccion = socios.copiar();
        // La búsqueda exacta por código utiliza el índice balanceado.
        Socio exacto = filtro.startsWith("sm-") ? buscarCodigo(filtro) : null;
        ArregloSocios resultados = new ArregloSocios(32);
        for (int i = 0; i < seleccion.tamanio(); i++) {
            Socio s = seleccion.get(i);
            if (activo != null && !activo.equals(s.getActivo())) continue;
            String campos = (s.getCodigoSocio()+" "+s.getNombre()+" "+s.getApellido()+" "+s.getDni()+" "+s.getTelefono()+" "+s.getCorreo()).toLowerCase(Locale.ROOT);
            if (exacto != null ? s != exacto : !campos.contains(filtro)) continue;
            resultados.agregar(s);
        }
        resultados.ordenar(Comparator.comparing(Socio::getIdSocio));
        var filas = new ArrayList<SocioDAO.RegistroSocio>();
        long inicio = (long) pagina * cantidad;
        for (long i = inicio; i < Math.min(resultados.tamanio(), inicio + cantidad); i++) {
            Socio s = resultados.get((int)i);
            filas.add(new SocioDAO.RegistroSocio(Math.toIntExact(s.getIdSocio()),s.getCodigoSocio(),s.getNombre(),s.getApellido(),s.getDni(),s.getTelefono(),s.getCorreo(),Boolean.TRUE.equals(s.getActivo())));
        }
        return new PageResult<>(filas,resultados.tamanio(),pagina,cantidad);
    }
}
