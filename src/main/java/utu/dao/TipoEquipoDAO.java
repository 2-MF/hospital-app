package utu.dao;

import utu.modelo.TipoEquipo;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEquipoDAO {

    private final Connection conexion;

    public TipoEquipoDAO() {
        conexion = Conexion.conectar();
    }

    private TipoEquipo mapearTipoEquipo(ResultSet rs) throws SQLException {
        TipoEquipo tipo = new TipoEquipo();
        tipo.setIdTipo(rs.getInt("id_tipo"));
        tipo.setNombre(rs.getString("nombre"));
        tipo.setDescripcion(rs.getString("descripcion"));
        tipo.setActivo(rs.getBoolean("activo"));
        return tipo;
    }

    public boolean insertar(TipoEquipo tipo) {
        String sql = "INSERT INTO tipo_equipo (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.setBoolean(3, tipo.isActivo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("tipo_equipo", "INSERT", "Alta de tipo de equipo '" + tipo.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(TipoEquipo tipo) {
        String sql = "UPDATE tipo_equipo SET nombre = ?, descripcion = ?, activo = ? WHERE id_tipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.setBoolean(3, tipo.isActivo());
            ps.setInt(4, tipo.getIdTipo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("tipo_equipo", "UPDATE", "Actualización de tipo de equipo '" + tipo.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE tipo_equipo SET activo = false WHERE id_tipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("tipo_equipo", "DELETE", "Baja lógica de tipo de equipo id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public TipoEquipo buscarPorId(int id) {
        String sql = "SELECT * FROM tipo_equipo WHERE id_tipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearTipoEquipo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TipoEquipo> listarTodos() {
        String sql = "SELECT * FROM tipo_equipo ORDER BY nombre";
        List<TipoEquipo> tipos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tipos.add(mapearTipoEquipo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
    }

    public List<TipoEquipo> listarActivas() {
        String sql = "SELECT * FROM tipo_equipo WHERE activo = true ORDER BY nombre";
        List<TipoEquipo> tipos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tipos.add(mapearTipoEquipo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
    }
}
