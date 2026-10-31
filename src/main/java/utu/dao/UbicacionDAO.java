package utu.dao;

import utu.modelo.Ubicacion;
import utu.modelo.Institucion;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UbicacionDAO {

    private final Connection conexion;

    public UbicacionDAO() {
        conexion = Conexion.conectar();
    }

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

    public boolean insertar(Ubicacion ubicacion) {
        String sql = "INSERT INTO ubicacion (nombre, descripcion, id_institucion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, ubicacion.getNombre());
            ps.setString(2, ubicacion.getDescripcion());
            ps.setInt(3, ubicacion.getInstitucion().getIdInstitucion());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("ubicacion", "INSERT", "Alta de ubicación '" + ubicacion.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Ubicacion ubicacion) {
        String sql = "UPDATE ubicacion SET nombre = ?, descripcion = ?, id_institucion = ? WHERE id_ubicacion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, ubicacion.getNombre());
            ps.setString(2, ubicacion.getDescripcion());
            ps.setInt(3, ubicacion.getInstitucion().getIdInstitucion());
            ps.setInt(4, ubicacion.getIdUbicacion());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("ubicacion", "UPDATE", "Actualización de ubicación '" + ubicacion.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM ubicacion WHERE id_ubicacion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("ubicacion", "DELETE", "Eliminación de ubicación id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Ubicacion buscarPorId(int id) {
        String sql = "SELECT u.*, i.nombre AS institucion_nombre FROM ubicacion u LEFT JOIN institucion i ON u.id_institucion = i.id_institucion WHERE u.id_ubicacion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUbicacion(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ubicacion> listarTodos() {
        String sql = "SELECT u.*, i.nombre AS institucion_nombre FROM ubicacion u LEFT JOIN institucion i ON u.id_institucion = i.id_institucion ORDER BY u.nombre";
        List<Ubicacion> ubicaciones = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ubicaciones.add(mapearUbicacion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ubicaciones;
    }
}
