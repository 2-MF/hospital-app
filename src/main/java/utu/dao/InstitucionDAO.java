package utu.dao;

import utu.modelo.Institucion;
import utu.modelo.Pais;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InstitucionDAO extends BaseDAO {

    private static final String SELECT = "SELECT i.*, pa.nombre AS pais_nombre FROM institucion i "
            + "INNER JOIN pais pa ON i.id_pais = pa.id_pais";

    private Institucion mapearInstitucion(ResultSet rs) throws SQLException {
        Institucion institucion = new Institucion();
        institucion.setIdInstitucion(rs.getInt("id_institucion"));
        institucion.setNombre(rs.getString("nombre"));
        institucion.setDireccion(rs.getString("direccion"));
        institucion.setTelefono(rs.getString("telefono"));
        institucion.setActivo(rs.getBoolean("activo"));
        Pais pais = new Pais();
        pais.setIdPais(rs.getInt("id_pais"));
        pais.setNombre(rs.getString("pais_nombre"));
        institucion.setPais(pais);
        return institucion;
    }

    private Object[] campos(Institucion i) {
        return new Object[]{i.getNombre(), i.getDireccion(), i.getTelefono(), i.getPais().getIdPais(), i.isActivo()};
    }

    public boolean insertar(Institucion institucion) {
        return insertarAuditado(
                "INSERT INTO institucion (nombre, direccion, telefono, id_pais, activo) VALUES (?, ?, ?, ?, ?)",
                "institucion", "Alta de institución '" + institucion.getNombre() + "'", campos(institucion)) >= 0;
    }

    public boolean actualizar(Institucion institucion) {
        return actualizarAuditado(
                "UPDATE institucion SET nombre = ?, direccion = ?, telefono = ?, id_pais = ?, activo = ? WHERE id_institucion = ?",
                "institucion", "Actualización de institución '" + institucion.getNombre() + "'",
                con(campos(institucion), institucion.getIdInstitucion()));
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE institucion SET activo = false WHERE id_institucion = ?",
                "institucion", "Baja lógica de institución id=" + id, id);
    }

    public Institucion buscarPorId(int id) {
        return buscar(SELECT + " WHERE i.id_institucion = ?", this::mapearInstitucion, id);
    }

    public List<Institucion> listarTodos() {
        return listar(SELECT + " ORDER BY i.nombre", this::mapearInstitucion);
    }

    public List<Institucion> listarActivas() {
        return listar(SELECT + " WHERE i.activo = true ORDER BY i.nombre", this::mapearInstitucion);
    }
}
