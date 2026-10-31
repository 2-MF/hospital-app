package utu.dao;

import utu.modelo.Institucion;
import utu.modelo.Pais;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstitucionDAO {

    private final Connection conexion;

    public InstitucionDAO() {
        conexion = Conexion.conectar();
    }

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

    public boolean insertar(Institucion institucion) {
        String sql = "INSERT INTO institucion (nombre, direccion, telefono, id_pais, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, institucion.getNombre());
            ps.setString(2, institucion.getDireccion());
            ps.setString(3, institucion.getTelefono());
            ps.setInt(4, institucion.getPais().getIdPais());
            ps.setBoolean(5, institucion.isActivo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("institucion", "INSERT", "Alta de institución '" + institucion.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Institucion institucion) {
        String sql = "UPDATE institucion SET nombre = ?, direccion = ?, telefono = ?, id_pais = ?, activo = ? WHERE id_institucion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, institucion.getNombre());
            ps.setString(2, institucion.getDireccion());
            ps.setString(3, institucion.getTelefono());
            ps.setInt(4, institucion.getPais().getIdPais());
            ps.setBoolean(5, institucion.isActivo());
            ps.setInt(6, institucion.getIdInstitucion());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("institucion", "UPDATE", "Actualización de institución '" + institucion.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE institucion SET activo = false WHERE id_institucion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("institucion", "DELETE", "Baja lógica de institución id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Institucion buscarPorId(int id) {
        String sql = "SELECT i.*, pa.nombre AS pais_nombre FROM institucion i INNER JOIN pais pa ON i.id_pais = pa.id_pais WHERE i.id_institucion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearInstitucion(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Institucion> listarTodos() {
        String sql = "SELECT i.*, pa.nombre AS pais_nombre FROM institucion i INNER JOIN pais pa ON i.id_pais = pa.id_pais ORDER BY i.nombre";
        List<Institucion> instituciones = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                instituciones.add(mapearInstitucion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instituciones;
    }

    public List<Institucion> listarActivas() {
        String sql = "SELECT i.*, pa.nombre AS pais_nombre FROM institucion i INNER JOIN pais pa ON i.id_pais = pa.id_pais WHERE i.activo = true ORDER BY i.nombre";
        List<Institucion> instituciones = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                instituciones.add(mapearInstitucion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instituciones;
    }
}
