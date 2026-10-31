package utu.dao;

import utu.modelo.Equipo;
import utu.modelo.Intervencion;
import utu.modelo.TipoIntervencion;
import utu.modelo.Usuario;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IntervencionDAO {

    private final Connection conexion;

    public IntervencionDAO() {
        conexion = Conexion.conectar();
    }

    private Intervencion mapearIntervencion(ResultSet rs) throws SQLException {
        Intervencion i = new Intervencion();
        i.setIdIntervencion(rs.getInt("id_intervencion"));

        java.sql.Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            i.setFecha(fecha.toLocalDate());
        }

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

    public boolean insertar(Intervencion i) {
        String sql = "INSERT INTO intervencion (fecha, descripcion, costo, id_equipo, id_tipo_intervencion, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (i.getFecha() == null) {
                ps.setNull(1, java.sql.Types.DATE);
            } else {
                ps.setDate(1, java.sql.Date.valueOf(i.getFecha()));
            }
            ps.setString(2, i.getDescripcion());
            ps.setBigDecimal(3, i.getCosto());
            ps.setInt(4, i.getEquipo().getIdEquipo());
            ps.setInt(5, i.getTipoIntervencion().getIdTipoIntervencion());
            ps.setInt(6, i.getUsuario().getIdUsuario());

            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("intervencion", "INSERT", "Registro de intervención sobre '" + i.getEquipo().getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Intervencion i) {
        String sql = "UPDATE intervencion SET fecha = ?, descripcion = ?, costo = ?, id_equipo = ?, id_tipo_intervencion = ?, id_usuario = ? WHERE id_intervencion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (i.getFecha() == null) {
                ps.setNull(1, java.sql.Types.DATE);
            } else {
                ps.setDate(1, java.sql.Date.valueOf(i.getFecha()));
            }
            ps.setString(2, i.getDescripcion());
            ps.setBigDecimal(3, i.getCosto());
            ps.setInt(4, i.getEquipo().getIdEquipo());
            ps.setInt(5, i.getTipoIntervencion().getIdTipoIntervencion());
            ps.setInt(6, i.getUsuario().getIdUsuario());
            ps.setInt(7, i.getIdIntervencion());

            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("intervencion", "UPDATE", "Actualización de intervención id=" + i.getIdIntervencion());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM intervencion WHERE id_intervencion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("intervencion", "DELETE", "Eliminación de intervención id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Intervencion buscarPorId(int id) {
        String sql = "SELECT i.*, eq.nombre AS equipo_nombre, ti.nombre AS tipo_intervencion_nombre, u.nombre AS usuario_nombre, u.apellido AS usuario_apellido FROM intervencion i LEFT JOIN equipo eq ON i.id_equipo = eq.id_equipo LEFT JOIN tipo_intervencion ti ON i.id_tipo_intervencion = ti.id_tipo_intervencion LEFT JOIN usuario u ON i.id_usuario = u.id_usuario WHERE i.id_intervencion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearIntervencion(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Intervencion> listarTodos() {
        String sql = "SELECT i.*, eq.nombre AS equipo_nombre, ti.nombre AS tipo_intervencion_nombre, u.nombre AS usuario_nombre, u.apellido AS usuario_apellido FROM intervencion i LEFT JOIN equipo eq ON i.id_equipo = eq.id_equipo LEFT JOIN tipo_intervencion ti ON i.id_tipo_intervencion = ti.id_tipo_intervencion LEFT JOIN usuario u ON i.id_usuario = u.id_usuario ORDER BY i.id_intervencion DESC";
        List<Intervencion> intervenciones = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                intervenciones.add(mapearIntervencion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return intervenciones;
    }
}
