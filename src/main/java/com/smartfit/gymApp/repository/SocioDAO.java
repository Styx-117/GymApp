package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@org.springframework.stereotype.Repository
public class SocioDAO {
    public record RegistroSocio(int id, String codigo, String nombre, String apellido,
                                String dni, String telefono, String correo, boolean activo) {}

    /** Formato conservado para clientes anteriores: id, nombre, apellido, DNI, teléfono, correo. */
    public List<String[]> obtenerClientes() {
        List<String[]> filas = new ArrayList<>();
        for (RegistroSocio socio : buscarClientes("", null)) {
            filas.add(new String[]{String.valueOf(socio.id()), socio.nombre(), socio.apellido(),
                    socio.dni(), socio.telefono(), socio.correo()});
        }
        return filas;
    }

    /** null incluye ambos estados. La búsqueda es literal, sin comodines SQL. */
    public List<RegistroSocio> buscarClientes(String texto, Boolean activo) {
        String sql = "SELECT id_socio, codigo_socio, nombre, apellido, dni, telefono, correo, "
                + "COALESCE(activo, false) AS activo FROM socio WHERE "
                + "POSITION(? IN LOWER(CONCAT_WS(' ', id_socio::text, codigo_socio, nombre, "
                + "apellido, dni, telefono, correo))) > 0"
                + (activo == null ? "" : " AND COALESCE(activo, false) = ?")
                + " ORDER BY id_socio";
        List<RegistroSocio> socios = new ArrayList<>();
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setQueryTimeout(15);
            stmt.setString(1, texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT));
            if (activo != null) stmt.setBoolean(2, activo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) socios.add(new RegistroSocio(rs.getInt("id_socio"),
                        rs.getString("codigo_socio"), rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("dni"), rs.getString("telefono"), rs.getString("correo"),
                        rs.getBoolean("activo")));
            }
            return socios;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el directorio de socios.", e);
        }
    }

    public boolean registrarCliente(String nombre, String apellido, String dni,
                                     String telefono, String correo) {
        // La secuencia identifica la fila y genera su código; no depende de MAX + 1.
        String sql = "WITH siguiente AS (SELECT nextval(pg_get_serial_sequence('socio','id_socio')) AS id) "
                + "INSERT INTO socio (id_socio, codigo_socio, nombre, apellido, dni, telefono, correo, activo, fecha_registro) "
                + "SELECT id, 'SM-' || LPAD(id::text, GREATEST(4, LENGTH(id::text)), '0'), "
                + "?, ?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP FROM siguiente";
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, dni);
            stmt.setString(4, telefono);
            stmt.setString(5, correo);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo registrar el socio. SQLSTATE=" + e.getSQLState());
            return false;
        }
    }

    public boolean actualizarCliente(int idSocio, String nombre, String apellido,
                                      String dni, String telefono, String correo) {
        String sql = "UPDATE socio SET nombre=?, apellido=?, dni=?, telefono=?, correo=? WHERE id_socio=?";
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, dni);
            stmt.setString(4, telefono);
            stmt.setString(5, correo);
            stmt.setInt(6, idSocio);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar el socio. SQLSTATE=" + e.getSQLState());
            return false;
        }
    }

    public boolean cambiarEstado(int idSocio, boolean activo) {
        try (Connection conn = conexion(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE socio SET activo=? WHERE id_socio=? AND COALESCE(activo, false) <> ?")) {
            stmt.setBoolean(1, activo);
            stmt.setInt(2, idSocio);
            stmt.setBoolean(3, activo);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo cambiar el estado del socio. SQLSTATE=" + e.getSQLState());
            return false;
        }
    }

    /** Compatibilidad: una baja conserva la fila y todas sus relaciones. */
    public boolean eliminarCliente(int idSocio) {
        return cambiarEstado(idSocio, false);
    }


    public PageResult<RegistroSocio> buscarPagina(String texto, Boolean activo, int pagina, int tamanio) {
        if(pagina<0||tamanio<1||tamanio>200)throw new IllegalArgumentException("Pagina o cantidad de registros no valida.");
        String cols="id_socio,codigo_socio,nombre,apellido,dni,telefono,correo,activo";
        String sql="WITH filtrados AS (SELECT "+cols+" FROM socio WHERE "
                +"POSITION(? IN LOWER(CONCAT_WS(' ',id_socio::text,codigo_socio,nombre,apellido,dni,telefono,correo)))>0"
                +(activo==null?"":" AND COALESCE(activo,false)=?")
                +"), pagina AS (SELECT "+cols+" FROM filtrados ORDER BY id_socio LIMIT ? OFFSET ?) "
                +"SELECT p.id_socio,p.codigo_socio,p.nombre,p.apellido,p.dni,p.telefono,p.correo,p.activo,"
                +"(SELECT count(*) FROM filtrados) AS total FROM (VALUES(1)) ancla(n) LEFT JOIN pagina p ON true ORDER BY p.id_socio";
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setQueryTimeout(15);int i=1;ps.setString(i++,texto==null?"":texto.trim().toLowerCase(Locale.ROOT));
            if(activo!=null)ps.setBoolean(i++,activo);ps.setInt(i++,tamanio);ps.setLong(i,(long)pagina*tamanio);
            List<RegistroSocio> registros=new ArrayList<>();long total=0;
            try(ResultSet rs=ps.executeQuery()){while(rs.next()) {total=rs.getLong("total");if(rs.getObject("id_socio")!=null)
                registros.add(new RegistroSocio(rs.getInt("id_socio"),rs.getString("codigo_socio"),rs.getString("nombre"),rs.getString("apellido"),rs.getString("dni"),rs.getString("telefono"),rs.getString("correo"),rs.getBoolean("activo")));}}
            return new PageResult<>(registros,total,pagina,tamanio);
        }catch(SQLException ex){throw new IllegalStateException("No se pudo consultar la pagina de socios.",ex);}
    }

    public List<com.smartfit.gymApp.model.Socio> listarSocios() {
        String sql="SELECT id_socio,codigo_socio,nombre,apellido,dni,telefono,correo,fecha_nacimiento,direccion,fecha_registro,activo FROM socio ORDER BY id_socio";
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setQueryTimeout(15);List<com.smartfit.gymApp.model.Socio> lista=new ArrayList<>();
            try(ResultSet rs=ps.executeQuery()){while(rs.next())lista.add(mapearSocio(rs));}return lista;
        }catch(SQLException ex){throw new IllegalStateException("No se pudo consultar el listado de socios.",ex);}
    }

    public com.smartfit.gymApp.model.Socio buscarPorId(long id) {return consultarSocio("id_socio",id);}
    public com.smartfit.gymApp.model.Socio buscarPorDni(String dni) {return consultarSocio("dni",dni);}
    private com.smartfit.gymApp.model.Socio consultarSocio(String columna,Object valor) {
        String sql="SELECT id_socio,codigo_socio,nombre,apellido,dni,telefono,correo,fecha_nacimiento,direccion,fecha_registro,activo FROM socio WHERE "+columna+"=?";
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setQueryTimeout(15);ps.setObject(1,valor);try(ResultSet rs=ps.executeQuery()){return rs.next()?mapearSocio(rs):null;}
        }catch(SQLException ex){throw new IllegalStateException("No se pudo consultar el socio.",ex);}
    }

    /** Mantiene todos los campos de la API y genera ID/codigo de alta desde la misma secuencia que Swing. */
    public com.smartfit.gymApp.model.Socio guardarSocio(com.smartfit.gymApp.model.Socio socio) {
        boolean nuevo=socio.getIdSocio()==null;
        String campos="id_socio,codigo_socio,nombre,apellido,dni,telefono,correo,fecha_nacimiento,direccion,fecha_registro,activo";
        String sql=nuevo
            ?"WITH siguiente AS (SELECT nextval(pg_get_serial_sequence('socio','id_socio')) AS id) INSERT INTO socio "
                +"("+campos+") SELECT id,'SM-'||LPAD(id::text,GREATEST(4,LENGTH(id::text)),'0'),?,?,?,?,?,?,?,CURRENT_TIMESTAMP,COALESCE(?,TRUE) FROM siguiente RETURNING "+campos
            :"UPDATE socio SET nombre=?,apellido=?,dni=?,telefono=?,correo=?,fecha_nacimiento=?,direccion=?,activo=COALESCE(?,activo) WHERE id_socio=? RETURNING "+campos;
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setQueryTimeout(15);ps.setString(1,socio.getNombre());ps.setString(2,socio.getApellido());ps.setString(3,socio.getDni());
            ps.setString(4,socio.getTelefono());ps.setString(5,socio.getCorreo());ps.setObject(6,socio.getFechaNacimiento());ps.setString(7,socio.getDireccion());ps.setObject(8,socio.getActivo());
            if(!nuevo)ps.setLong(9,socio.getIdSocio());
            try(ResultSet rs=ps.executeQuery()){if(!rs.next())throw new IllegalArgumentException("El socio ya no existe.");return mapearSocio(rs);}
        }catch(SQLException ex){if("23505".equals(ex.getSQLState()))throw new IllegalArgumentException("Ya existe un socio con ese DNI o codigo.");throw new IllegalStateException("No se pudo guardar el socio.",ex);}
    }
    private com.smartfit.gymApp.model.Socio mapearSocio(ResultSet rs)throws SQLException {
        var socio=new com.smartfit.gymApp.model.Socio(rs.getString("codigo_socio"),rs.getString("nombre"),rs.getString("apellido"),rs.getString("dni"));
        socio.setIdSocio(rs.getLong("id_socio"));socio.setTelefono(rs.getString("telefono"));socio.setCorreo(rs.getString("correo"));socio.setDireccion(rs.getString("direccion"));
        Date fecha=rs.getDate("fecha_nacimiento");socio.setFechaNacimiento(fecha==null?null:fecha.toLocalDate());Timestamp registro=rs.getTimestamp("fecha_registro");socio.setFechaRegistro(registro==null?null:registro.toLocalDateTime());socio.setActivo(rs.getBoolean("activo"));return socio;
    }

    private Connection conexion() throws SQLException {
        Connection conn = ConexionBD.conectar();
        if (conn == null) throw new SQLException("Conexión no disponible", "08001");
        return conn;
    }
}
