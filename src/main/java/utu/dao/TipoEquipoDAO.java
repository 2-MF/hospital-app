package utu.dao;

import utu.modelo.TipoEquipo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TipoEquipoDAO extends BaseDAO {

    private TipoEquipo mapearTipoEquipo(ResultSet rs) throws SQLException {
        TipoEquipo tipo = new TipoEquipo();
        tipo.setIdTipo(rs.getInt("id_tipo"));
        tipo.setNombre(rs.getString("nombre"));
        tipo.setDescripcion(rs.getString("descripcion"));
        tipo.setActivo(rs.getBoolean("activo"));
        return tipo;
    }

    public boolean insertar(TipoEquipo tipo) {
        return insertarAuditado("INSERT INTO tipo_equipo (nombre, descripcion, activo) VALUES (?, ?, ?)",
                "tipo_equipo", "Alta de tipo de equipo '" + tipo.getNombre() + "'",
                tipo.getNombre(), tipo.getDescripcion(), tipo.isActivo()) >= 0;
    }

    public boolean actualizar(TipoEquipo tipo) {
        return actualizarAuditado("UPDATE tipo_equipo SET nombre = ?, descripcion = ?, activo = ? WHERE id_tipo = ?",
                "tipo_equipo", "Actualización de tipo de equipo '" + tipo.getNombre() + "'",
                tipo.getNombre(), tipo.getDescripcion(), tipo.isActivo(), tipo.getIdTipo());
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE tipo_equipo SET activo = false WHERE id_tipo = ?",
                "tipo_equipo", "Baja lógica de tipo de equipo id=" + id, id);
    }

    public TipoEquipo buscarPorId(int id) {
        return buscar("SELECT * FROM tipo_equipo WHERE id_tipo = ?", this::mapearTipoEquipo, id);
    }

    public List<TipoEquipo> listarTodos() {
        return listar("SELECT * FROM tipo_equipo ORDER BY nombre", this::mapearTipoEquipo);
    }

    public List<TipoEquipo> listarActivas() {
        return listar("SELECT * FROM tipo_equipo WHERE activo = true ORDER BY nombre", this::mapearTipoEquipo);
    }
}
