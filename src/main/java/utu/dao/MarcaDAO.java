package utu.dao;

import utu.modelo.Marca;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {

    private final Connection conexion;

    public MarcaDAO() {
        conexion = Conexion.conectar();
    }

    private Marca mapearMarca(ResultSet rs) throws SQLException {
        Marca marca = new Marca();
        marca.setIdMarca(rs.getInt("id_marca"));
        marca.setNombre(rs.getString("nombre"));
        marca.setDescripcion(rs.getString("descripcion"));
        marca.setActivo(rs.getBoolean("activo"));
        return marca;
    }

    public boolean insertar(Marca marca) {
        String sql = "INSERT INTO marca (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, marca.getNombre());
            ps.setString(2, marca.getDescripcion());
            ps.setBoolean(3, marca.isActivo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("marca", "INSERT", "Alta de marca '" + marca.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Marca marca) {
        String sql = "UPDATE marca SET nombre = ?, descripcion = ?, activo = ? WHERE id_marca = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, marca.getNombre());
            ps.setString(2, marca.getDescripcion());
            ps.setBoolean(3, marca.isActivo());
            ps.setInt(4, marca.getIdMarca());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("marca", "UPDATE", "Actualización de marca '" + marca.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE marca SET activo = false WHERE id_marca = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("marca", "DELETE", "Baja lógica de marca id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Marca buscarPorId(int id) {
        String sql = "SELECT * FROM marca WHERE id_marca = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearMarca(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Marca> listarTodos() {
        String sql = "SELECT * FROM marca ORDER BY nombre";
        List<Marca> marcas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                marcas.add(mapearMarca(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marcas;
    }

    public List<Marca> listarActivas() {
        String sql = "SELECT * FROM marca WHERE activo = true ORDER BY nombre";
        List<Marca> marcas = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                marcas.add(mapearMarca(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marcas;
    }

    public boolean existe(String nombre) {
        String sql = "SELECT COUNT(*) FROM marca WHERE UPPER(nombre) = UPPER(?)";
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
