package utu.dao;

import utu.modelo.Modelo;
import utu.modelo.Marca;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModeloDAO {

    private final Connection conexion;

    public ModeloDAO() {
        conexion = Conexion.conectar();
    }

    private Modelo mapearModelo(ResultSet rs) throws SQLException {
        Modelo modelo = new Modelo();
        modelo.setIdModelo(rs.getInt("id_modelo"));
        modelo.setNombre(rs.getString("nombre"));
        modelo.setActivo(rs.getBoolean("activo"));
        Marca marca = new Marca();
        marca.setIdMarca(rs.getInt("id_marca"));
        marca.setNombre(rs.getString("marca_nombre"));
        modelo.setMarca(marca);
        return modelo;
    }

    public boolean insertar(Modelo modelo) {
        String sql = "INSERT INTO modelo (nombre, id_marca, activo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, modelo.getNombre());
            ps.setInt(2, modelo.getMarca().getIdMarca());
            ps.setBoolean(3, modelo.isActivo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("modelo", "INSERT", "Alta de modelo '" + modelo.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Modelo modelo) {
        String sql = "UPDATE modelo SET nombre = ?, id_marca = ?, activo = ? WHERE id_modelo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, modelo.getNombre());
            ps.setInt(2, modelo.getMarca().getIdMarca());
            ps.setBoolean(3, modelo.isActivo());
            ps.setInt(4, modelo.getIdModelo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("modelo", "UPDATE", "Actualización de modelo '" + modelo.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE modelo SET activo = false WHERE id_modelo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("modelo", "DELETE", "Baja lógica de modelo id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Modelo buscarPorId(int id) {
        String sql = "SELECT m.*, ma.nombre AS marca_nombre FROM modelo m LEFT JOIN marca ma ON m.id_marca = ma.id_marca WHERE m.id_modelo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearModelo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Modelo> listarTodos() {
        String sql = "SELECT m.*, ma.nombre AS marca_nombre FROM modelo m LEFT JOIN marca ma ON m.id_marca = ma.id_marca ORDER BY m.nombre";
        List<Modelo> modelos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelos.add(mapearModelo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelos;
    }

    public List<Modelo> listarActivas() {
        String sql = "SELECT m.*, ma.nombre AS marca_nombre FROM modelo m LEFT JOIN marca ma ON m.id_marca = ma.id_marca WHERE m.activo = true ORDER BY m.nombre";
        List<Modelo> modelos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelos.add(mapearModelo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelos;
    }
}
