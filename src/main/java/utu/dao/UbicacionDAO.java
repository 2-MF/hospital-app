package utu.dao;

import utu.modelo.Institucion;
import utu.modelo.Ubicacion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UbicacionDAO extends BaseDAO {

    private static final String SELECT = "SELECT u.*, i.nombre AS institucion_nombre FROM ubicacion u "
            + "LEFT JOIN institucion i ON u.id_institucion = i.id_institucion";

    private Ubicacion mapearUbicacion(ResultSet rs) throws SQLException {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setIdUbicacion(rs.getInt("id_ubicacion"));
        ubicacion.setNombre(rs.getString("nombre"));
        ubicacion.setDescripcion(rs.getString("descripcion"));
        Institucion institucion = new Institucion();
        institucion.setIdInstitucion(rs.getInt("id_institucion"));
        institucion.setNombre(rs.getString("institucion_nombre"));
        ubicacion.setInstitucion(institucion);
        return ubicacion;
    }

    private Object[] campos(Ubicacion u) {
        return new Object[]{u.getNombre(), u.getDescripcion(), u.getInstitucion().getIdInstitucion()};
    }

    public boolean insertar(Ubicacion ubicacion) {
        return insertarAuditado("INSERT INTO ubicacion (nombre, descripcion, id_institucion) VALUES (?, ?, ?)",
                "ubicacion", "Alta de ubicación '" + ubicacion.getNombre() + "'", campos(ubicacion)) >= 0;
    }

    public boolean actualizar(Ubicacion ubicacion) {
        return actualizarAuditado(
                "UPDATE ubicacion SET nombre = ?, descripcion = ?, id_institucion = ? WHERE id_ubicacion = ?",
                "ubicacion", "Actualización de ubicación '" + ubicacion.getNombre() + "'",
                con(campos(ubicacion), ubicacion.getIdUbicacion()));
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("DELETE FROM ubicacion WHERE id_ubicacion = ?",
                "ubicacion", "Eliminación de ubicación id=" + id, id);
    }

    public Ubicacion buscarPorId(int id) {
        return buscar(SELECT + " WHERE u.id_ubicacion = ?", this::mapearUbicacion, id);
    }

    public List<Ubicacion> listarTodos() {
        return listar(SELECT + " ORDER BY u.nombre", this::mapearUbicacion);
    }
}
