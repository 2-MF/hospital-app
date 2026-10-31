package utu.dao;

import utu.modelo.TipoIntervencion;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoIntervencionDAO {

    private final Connection conexion;

    public TipoIntervencionDAO() {
        conexion = Conexion.conectar();
    }

    private TipoIntervencion mapearTipoIntervencion(ResultSet rs) throws SQLException {
        TipoIntervencion tipoIntervencion = new TipoIntervencion();
        tipoIntervencion.setIdTipoIntervencion(rs.getInt("id_tipo_intervencion"));
        tipoIntervencion.setNombre(rs.getString("nombre"));
        tipoIntervencion.setDescripcion(rs.getString("descripcion"));
        return tipoIntervencion;
    }

    public boolean insertar(TipoIntervencion tipoIntervencion) {
        String sql = "INSERT INTO tipo_intervencion (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tipoIntervencion.getNombre());
            ps.setString(2, tipoIntervencion.getDescripcion());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("tipo_intervencion", "INSERT", "Alta de tipo de intervención '" + tipoIntervencion.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(TipoIntervencion tipoIntervencion) {
        String sql = "UPDATE tipo_intervencion SET nombre = ?, descripcion = ? WHERE id_tipo_intervencion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tipoIntervencion.getNombre());
            ps.setString(2, tipoIntervencion.getDescripcion());
            ps.setInt(3, tipoIntervencion.getIdTipoIntervencion());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("tipo_intervencion", "UPDATE", "Actualización de tipo de intervención '" + tipoIntervencion.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM tipo_intervencion WHERE id_tipo_intervencion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("tipo_intervencion", "DELETE", "Eliminación de tipo de intervención id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<TipoIntervencion> listarTodos() {
        String sql = "SELECT * FROM tipo_intervencion ORDER BY nombre";
        List<TipoIntervencion> tiposIntervencion = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tiposIntervencion.add(mapearTipoIntervencion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tiposIntervencion;
    }

    public boolean existe(String nombre) {
        String sql = "SELECT COUNT(*) FROM tipo_intervencion WHERE UPPER(nombre) = UPPER(?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
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
