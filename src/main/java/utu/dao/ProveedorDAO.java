package utu.dao;

import utu.modelo.Proveedor;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    private final Connection conexion;

    public ProveedorDAO() {
        conexion = Conexion.conectar();
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(rs.getInt("id_proveedor"));
        proveedor.setNombre(rs.getString("nombre"));
        proveedor.setDireccion(rs.getString("direccion"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setEmail(rs.getString("email"));
        proveedor.setActivo(rs.getBoolean("activo"));
        return proveedor;
    }

    public boolean insertar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedor (nombre, direccion, telefono, email, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getDireccion());
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getEmail());
            ps.setBoolean(5, proveedor.isActivo());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("proveedor", "INSERT", "Alta de proveedor '" + proveedor.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedor SET nombre = ?, direccion = ?, telefono = ?, email = ?, activo = ? WHERE id_proveedor = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getDireccion());
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getEmail());
            ps.setBoolean(5, proveedor.isActivo());
            ps.setInt(6, proveedor.getIdProveedor());
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("proveedor", "UPDATE", "Actualización de proveedor '" + proveedor.getNombre() + "'");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE proveedor SET activo = false WHERE id_proveedor = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("proveedor", "DELETE", "Baja lógica de proveedor id=" + id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Proveedor buscarPorId(int id) {
        String sql = "SELECT * FROM proveedor WHERE id_proveedor = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearProveedor(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Proveedor> listarTodos() {
        String sql = "SELECT * FROM proveedor ORDER BY nombre";
        List<Proveedor> proveedores = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    public List<Proveedor> listarActivas() {
        String sql = "SELECT * FROM proveedor WHERE activo = true ORDER BY nombre";
        List<Proveedor> proveedores = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }
}
