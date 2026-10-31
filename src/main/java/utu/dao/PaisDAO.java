package utu.dao;

import utu.modelo.Pais;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaisDAO {

    private final Connection conexion;

    public PaisDAO() {
        conexion = Conexion.conectar();
    }

    private Pais mapearPais(ResultSet rs) throws SQLException {
        Pais pais = new Pais();
        pais.setIdPais(rs.getInt("id_pais"));
        pais.setNombre(rs.getString("nombre"));
        return pais;
    }

    public boolean insertar(Pais pais) {
        String sql = "INSERT INTO pais (nombre) VALUES (?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, pais.getNombre());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("pais", "INSERT", "Alta de país '" + pais.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Pais pais) {
        String sql = "UPDATE pais SET nombre = ? WHERE id_pais = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, pais.getNombre());
            ps.setInt(2, pais.getIdPais());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("pais", "UPDATE", "Actualización de país '" + pais.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM pais WHERE id_pais = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("pais", "DELETE", "Eliminación de país id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Pais buscarPorId(int id) {
        String sql = "SELECT * FROM pais WHERE id_pais = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearPais(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Pais> listarTodos() {
        String sql = "SELECT * FROM pais ORDER BY nombre";
        List<Pais> paises = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paises.add(mapearPais(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paises;
    }
}
