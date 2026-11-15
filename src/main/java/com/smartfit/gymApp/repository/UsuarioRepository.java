package com.smartfit.gymApp.repository;

import com.smartfit.gymApp.config.ConexionBD;
import com.smartfit.gymApp.model.Usuario;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.*;

@Repository
public class UsuarioRepository {
    private static final String CAMPOS="id_usuario,nombre_usuario,contrasena,estado";
    public boolean verificarCredenciales(String usuario,String password){return autenticar(usuario,password)!=null;}
    public Usuario autenticar(String nombre,String contrasena){
        String sql="SELECT id_usuario,nombre_usuario,estado FROM usuario WHERE nombre_usuario=? AND contrasena=? AND COALESCE(estado,TRUE)=TRUE";
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setQueryTimeout(15);ps.setString(1,nombre);ps.setString(2,contrasena);
            try(ResultSet rs=ps.executeQuery()){if(!rs.next())return null;Usuario u=new Usuario();u.setId(rs.getLong(1));u.setNombre(rs.getString(2));u.setEstado(rs.getBoolean(3));return u;}
        }catch(SQLException ex){throw fallo("consultar la cuenta",ex);}
    }
    public List<Usuario> findAll(){
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement("SELECT "+CAMPOS+" FROM usuario ORDER BY id_usuario")){
            ps.setQueryTimeout(15);List<Usuario> lista=new ArrayList<>();try(ResultSet rs=ps.executeQuery()){while(rs.next())lista.add(mapear(rs));}return lista;
        }catch(SQLException ex){throw fallo("consultar los usuarios",ex);}
    }
    public Usuario save(Usuario u){
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement("INSERT INTO usuario(nombre_usuario,contrasena,estado) VALUES(?,?,?) RETURNING id_usuario")){
            ps.setQueryTimeout(15);ps.setString(1,u.getNombre());ps.setString(2,u.getPassword());ps.setBoolean(3,!Boolean.FALSE.equals(u.getEstado()));
            try(ResultSet rs=ps.executeQuery()){rs.next();u.setId(rs.getLong(1));}return u;
        }catch(SQLException ex){if("23505".equals(ex.getSQLState()))throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");throw fallo("guardar el usuario",ex);}
    }
    public Optional<Usuario> findById(Long id){return buscar("id_usuario",id);}
    public Optional<Usuario> findByNombreUsuario(String nombre){return buscar("nombre_usuario",nombre);}
    private Optional<Usuario> buscar(String columna,Object valor){
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement("SELECT "+CAMPOS+" FROM usuario WHERE "+columna+"=?")){
            ps.setQueryTimeout(15);ps.setObject(1,valor);try(ResultSet rs=ps.executeQuery()){return rs.next()?Optional.of(mapear(rs)):Optional.empty();}
        }catch(SQLException ex){throw fallo("consultar el usuario",ex);}
    }
    public boolean existsById(Long id){return findById(id).isPresent();}
    public void deleteById(Long id){
        try(Connection conn=conexion();PreparedStatement ps=conn.prepareStatement("UPDATE usuario SET estado=FALSE WHERE id_usuario=?")){
            ps.setQueryTimeout(15);ps.setLong(1,id);ps.executeUpdate();
        }catch(SQLException ex){throw fallo("desactivar el usuario",ex);}
    }
    private Usuario mapear(ResultSet rs)throws SQLException{Usuario u=new Usuario();u.setId(rs.getLong("id_usuario"));u.setNombre(rs.getString("nombre_usuario"));u.setPassword(rs.getString("contrasena"));u.setEstado(rs.getBoolean("estado"));return u;}
    private Connection conexion()throws SQLException{Connection conn=ConexionBD.conectar();if(conn==null)throw new SQLException("Conexion no disponible","08001");return conn;}
    private IllegalStateException fallo(String accion,SQLException ex){return new IllegalStateException("No se pudo "+accion+". Revisa la conexion.",ex);}
}
