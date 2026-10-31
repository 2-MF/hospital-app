package utu.dao;

import utu.modelo.Perfil;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO {

    private final Connection conexion;

    public PerfilDAO() {
        conexion = Conexion.conectar();
    }

    private Perfil mapearPerfil(ResultSet rs) throws SQLException {
        Perfil perfil = new Perfil();
        perfil.setIdPerfil(rs.getInt("id_perfil"));
        perfil.setNombre(rs.getString("nombre"));
        perfil.setDescripcion(rs.getString("descripcion"));
        perfil.setActivo(rs.getBoolean("activo"));
        return perfil;
    }

    public boolean insertar(Perfil perfil) {
        String sql = "INSERT INTO perfil (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, perfil.getNombre());
            ps.setString(2, perfil.getDescripcion());
            ps.setBoolean(3, perfil.isActivo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("perfil", "INSERT", "Alta de perfil '" + perfil.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Perfil perfil) {
        String sql = "UPDATE perfil SET nombre = ?, descripcion = ?, activo = ? WHERE id_perfil = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, perfil.getNombre());
            ps.setString(2, perfil.getDescripcion());
            ps.setBoolean(3, perfil.isActivo());
            ps.setInt(4, perfil.getIdPerfil());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("perfil", "UPDATE", "Actualización de perfil '" + perfil.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE perfil SET activo = false WHERE id_perfil = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("perfil", "DELETE", "Baja lógica de perfil id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Perfil buscarPorId(int id) {
        String sql = "SELECT * FROM perfil WHERE id_perfil = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearPerfil(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Perfil> listarTodos() {
        String sql = "SELECT * FROM perfil ORDER BY nombre";
        List<Perfil> perfiles = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                perfiles.add(mapearPerfil(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perfiles;
    }

    public List<Perfil> listarActivas() {
        String sql = "SELECT * FROM perfil WHERE activo = true ORDER BY nombre";
        List<Perfil> perfiles = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                perfiles.add(mapearPerfil(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perfiles;
    }

    public boolean existe(String nombre) {
        String sql = "SELECT COUNT(*) FROM perfil WHERE UPPER(nombre) = UPPER(?)";
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
