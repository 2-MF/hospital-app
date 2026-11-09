package utu.dao;

import utu.modelo.Equipo;
import utu.modelo.Intervencion;
import utu.modelo.TipoIntervencion;
import utu.modelo.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class IntervencionDAO extends BaseDAO {

    private static final String SELECT = "SELECT i.*, eq.nombre AS equipo_nombre, ti.nombre AS tipo_intervencion_nombre, "
            + "u.nombre AS usuario_nombre, u.apellido AS usuario_apellido FROM intervencion i "
            + "LEFT JOIN equipo eq ON i.id_equipo = eq.id_equipo "
            + "LEFT JOIN tipo_intervencion ti ON i.id_tipo_intervencion = ti.id_tipo_intervencion "
            + "LEFT JOIN usuario u ON i.id_usuario = u.id_usuario";

    private Intervencion mapearIntervencion(ResultSet rs) throws SQLException {
        Intervencion i = new Intervencion();
        i.setIdIntervencion(rs.getInt("id_intervencion"));
        i.setFecha(fecha(rs, "fecha"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setCosto(rs.getBigDecimal("costo"));

        Equipo equipo = new Equipo();
        equipo.setIdEquipo(rs.getInt("id_equipo"));
        equipo.setNombre(rs.getString("equipo_nombre"));
        i.setEquipo(equipo);

        TipoIntervencion tipoIntervencion = new TipoIntervencion();
        tipoIntervencion.setIdTipoIntervencion(rs.getInt("id_tipo_intervencion"));
        tipoIntervencion.setNombre(rs.getString("tipo_intervencion_nombre"));
        i.setTipoIntervencion(tipoIntervencion);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("usuario_nombre"));
        usuario.setApellido(rs.getString("usuario_apellido"));
        i.setUsuario(usuario);

        return i;
    }

    private Object[] campos(Intervencion i) {
        return new Object[]{i.getFecha(), i.getDescripcion(), i.getCosto(), i.getEquipo().getIdEquipo(),
                i.getTipoIntervencion().getIdTipoIntervencion(), i.getUsuario().getIdUsuario()};
    }

    public boolean insertar(Intervencion i) {
        return insertarAuditado(
                "INSERT INTO intervencion (fecha, descripcion, costo, id_equipo, id_tipo_intervencion, id_usuario) VALUES (?, ?, ?, ?, ?, ?)",
                "intervencion", "Registro de intervención sobre '" + i.getEquipo().getNombre() + "'", campos(i)) >= 0;
    }

    public boolean actualizar(Intervencion i) {
        return actualizarAuditado(
                "UPDATE intervencion SET fecha = ?, descripcion = ?, costo = ?, id_equipo = ?, id_tipo_intervencion = ?, id_usuario = ? WHERE id_intervencion = ?",
                "intervencion", "Actualización de intervención id=" + i.getIdIntervencion(),
                con(campos(i), i.getIdIntervencion()));
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("DELETE FROM intervencion WHERE id_intervencion = ?",
                "intervencion", "Eliminación de intervención id=" + id, id);
    }

    public Intervencion buscarPorId(int id) {
        return buscar(SELECT + " WHERE i.id_intervencion = ?", this::mapearIntervencion, id);
    }

    public List<Intervencion> listarTodos() {
        return listar(SELECT + " ORDER BY i.id_intervencion DESC", this::mapearIntervencion);
    }
}
