package utu.dao;

import utu.modelo.Auditoria;
import utu.modelo.Usuario;
import utu.util.Sesion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class AuditoriaDAO extends BaseDAO {

    public boolean registrar(String tablaAfectada, String operacion, String descripcion) {
        Integer idUsuario = Sesion.getIdUsuario();
        return registrar(tablaAfectada, operacion, descripcion, idUsuario == null ? 0 : idUsuario);
    }

    public boolean registrar(String tablaAfectada, String operacion, String descripcion, int idUsuario) {
        return ejecutar("INSERT INTO auditoria (tabla_afectada, operacion, descripcion, id_usuario) VALUES (?, ?, ?, ?)",
                tablaAfectada, operacion, descripcion, idUsuario > 0 ? idUsuario : null);
    }

    public List<Auditoria> listarTodos() {
        return listar("SELECT a.*, u.nombre AS usuario_nombre, u.apellido AS usuario_apellido FROM auditoria a "
                + "LEFT JOIN usuario u ON a.id_usuario = u.id_usuario ORDER BY a.id_auditoria DESC",
                this::mapearAuditoria);
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
