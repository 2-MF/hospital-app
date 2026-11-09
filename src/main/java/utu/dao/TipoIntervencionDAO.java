package utu.dao;

import utu.modelo.TipoIntervencion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TipoIntervencionDAO extends BaseDAO {

    private TipoIntervencion mapearTipoIntervencion(ResultSet rs) throws SQLException {
        TipoIntervencion tipo = new TipoIntervencion();
        tipo.setIdTipoIntervencion(rs.getInt("id_tipo_intervencion"));
        tipo.setNombre(rs.getString("nombre"));
        tipo.setDescripcion(rs.getString("descripcion"));
        return tipo;
    }

    public boolean insertar(TipoIntervencion tipo) {
        return insertarAuditado("INSERT INTO tipo_intervencion (nombre, descripcion) VALUES (?, ?)",
                "tipo_intervencion", "Alta de tipo de intervención '" + tipo.getNombre() + "'",
                tipo.getNombre(), tipo.getDescripcion()) >= 0;
    }

    public boolean actualizar(TipoIntervencion tipo) {
        return actualizarAuditado("UPDATE tipo_intervencion SET nombre = ?, descripcion = ? WHERE id_tipo_intervencion = ?",
                "tipo_intervencion", "Actualización de tipo de intervención '" + tipo.getNombre() + "'",
                tipo.getNombre(), tipo.getDescripcion(), tipo.getIdTipoIntervencion());
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("DELETE FROM tipo_intervencion WHERE id_tipo_intervencion = ?",
                "tipo_intervencion", "Eliminación de tipo de intervención id=" + id, id);
    }

    public List<TipoIntervencion> listarTodos() {
        return listar("SELECT * FROM tipo_intervencion ORDER BY nombre", this::mapearTipoIntervencion);
    }

    public boolean existe(String nombre) {
        return hay("SELECT COUNT(*) FROM tipo_intervencion WHERE UPPER(nombre) = UPPER(?)", nombre);
    }
}
