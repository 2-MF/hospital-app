package utu.dao;

import utu.conexion.Conexion;
import utu.modelo.Auditoria;
import utu.modelo.Usuario;
import utu.util.Sesion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    private final Connection conexion;

    public AuditoriaDAO() {
        conexion = Conexion.conectar();
    }

    /** Registra un evento de auditoría con el usuario de la sesión actual. */
    public boolean registrar(String tablaAfectada, String operacion, String descripcion) {
        Integer idUsuario = Sesion.getIdUsuario();
        return registrar(tablaAfectada, operacion, descripcion, idUsuario == null ? 0 : idUsuario);
    }

    /** Registra un evento de auditoría con un id de usuario explícito (0 => sin usuario). */
    public boolean registrar(String tablaAfectada, String operacion, String descripcion, int idUsuario) {
        String sql = "INSERT INTO auditoria (tabla_afectada, operacion, descripcion, id_usuario) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tablaAfectada);
            ps.setString(2, operacion);
            ps.setString(3, descripcion);
            if (idUsuario > 0) {
                ps.setInt(4, idUsuario);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Auditoria> listarTodos() {
        String sql = "SELECT a.*, u.nombre AS usuario_nombre, u.apellido AS usuario_apellido FROM auditoria a LEFT JOIN usuario u ON a.id_usuario = u.id_usuario ORDER BY a.id_auditoria DESC";
        List<Auditoria> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearAuditoria(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Auditoria mapearAuditoria(ResultSet rs) throws SQLException {
        Auditoria auditoria = new Auditoria();
        auditoria.setIdAuditoria(rs.getInt("id_auditoria"));
        Timestamp fecha = rs.getTimestamp("fecha");
        auditoria.setFecha(fecha == null ? null : fecha.toLocalDateTime());
        auditoria.setTablaAfectada(rs.getString("tabla_afectada"));
        auditoria.setOperacion(rs.getString("operacion"));
        auditoria.setDescripcion(rs.getString("descripcion"));

        int idUsuario = rs.getInt("id_usuario");
        if (!rs.wasNull()) {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            usuario.setNombre(rs.getString("usuario_nombre"));
            usuario.setApellido(rs.getString("usuario_apellido"));
            auditoria.setUsuario(usuario);
        }
        return auditoria;
    }
}
