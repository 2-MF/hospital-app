package utu.dao;

import utu.modelo.Perfil;
import utu.modelo.Usuario;
import utu.conexion.Conexion;
import utu.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final Connection conexion;

    public UsuarioDAO() {
        conexion = Conexion.conectar();
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setCedula(rs.getString("cedula"));
        u.setTelefono(rs.getString("telefono"));
        u.setCorreo(rs.getString("correo"));
        u.setUsuario(rs.getString("usuario"));

        java.sql.Date fechaAlta = rs.getDate("fecha_alta");
        if (fechaAlta != null) {
            u.setFechaAlta(fechaAlta.toLocalDate());
        }

        java.sql.Date ultimoAcceso = rs.getDate("ultimo_acceso");
        if (ultimoAcceso != null) {
            u.setUltimoAcceso(ultimoAcceso.toLocalDate());
        }

        u.setActivo(rs.getBoolean("activo"));

        Perfil perfil = new Perfil();
        perfil.setIdPerfil(rs.getInt("id_perfil"));
        perfil.setNombre(rs.getString("perfil_nombre"));
        perfil.setDescripcion(rs.getString("perfil_descripcion"));
        perfil.setActivo(rs.getBoolean("perfil_activo"));
        u.setPerfil(perfil);

        return u;
    }

    public boolean insertar(Usuario u) {
        if (u.getContrasena() == null || u.getContrasena().isBlank()) {
            return false;
        }
        String hash = PasswordUtil.hash(u.getContrasena());
        String sql = "INSERT INTO usuario (nombre, apellido, cedula, telefono, correo, usuario, contrasena, fecha_alta, activo, id_perfil) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCedula());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.getUsuario());
            ps.setString(7, hash);
            ps.setDate(8, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setBoolean(9, u.isActivo());
            ps.setInt(10, u.getPerfil().getIdPerfil());

            if (ps.executeUpdate() > 0) {
                // Recupera el id generado para que el objeto quede sincronizado con la base
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setIdUsuario(rs.getInt(1));
                    }
                }
                new AuditoriaDAO().registrar("usuario", "INSERT", "Alta de usuario '" + u.getUsuario() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Usuario u) {
        boolean cambiarContrasena = u.getContrasena() != null && !u.getContrasena().isBlank();
        String sql;
        if (cambiarContrasena) {
            sql = "UPDATE usuario SET nombre = ?, apellido = ?, cedula = ?, telefono = ?, correo = ?, usuario = ?, contrasena = ?, activo = ?, id_perfil = ? WHERE id_usuario = ?";
        } else {
            sql = "UPDATE usuario SET nombre = ?, apellido = ?, cedula = ?, telefono = ?, correo = ?, usuario = ?, activo = ?, id_perfil = ? WHERE id_usuario = ?";
        }
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCedula());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getCorreo());
            ps.setString(6, u.getUsuario());
            int pos = 7;
            if (cambiarContrasena) {
                ps.setString(pos, PasswordUtil.hash(u.getContrasena()));
                pos++;
            }
            ps.setBoolean(pos, u.isActivo());
            pos++;
            ps.setInt(pos, u.getPerfil().getIdPerfil());
            pos++;
            ps.setInt(pos, u.getIdUsuario());

            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("usuario", "UPDATE", "Actualización de usuario '" + u.getUsuario() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE usuario SET activo = false WHERE id_usuario = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("usuario", "DELETE", "Baja lógica de usuario id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT u.*, p.nombre AS perfil_nombre, p.descripcion AS perfil_descripcion, p.activo AS perfil_activo FROM usuario u INNER JOIN perfil p ON u.id_perfil = p.id_perfil WHERE u.id_usuario = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT u.*, p.nombre AS perfil_nombre, p.descripcion AS perfil_descripcion, p.activo AS perfil_activo FROM usuario u INNER JOIN perfil p ON u.id_perfil = p.id_perfil ORDER BY u.usuario";
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE usuario = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
