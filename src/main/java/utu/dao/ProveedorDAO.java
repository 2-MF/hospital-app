package utu.dao;

import utu.modelo.Proveedor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProveedorDAO extends BaseDAO {

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

    private Object[] campos(Proveedor p) {
        return new Object[]{p.getNombre(), p.getDireccion(), p.getTelefono(), p.getEmail(), p.isActivo()};
    }

    public boolean insertar(Proveedor proveedor) {
        return insertarAuditado(
                "INSERT INTO proveedor (nombre, direccion, telefono, email, activo) VALUES (?, ?, ?, ?, ?)",
                "proveedor", "Alta de proveedor '" + proveedor.getNombre() + "'", campos(proveedor)) >= 0;
    }

    public boolean actualizar(Proveedor proveedor) {
        return actualizarAuditado(
                "UPDATE proveedor SET nombre = ?, direccion = ?, telefono = ?, email = ?, activo = ? WHERE id_proveedor = ?",
                "proveedor", "Actualización de proveedor '" + proveedor.getNombre() + "'",
                con(campos(proveedor), proveedor.getIdProveedor()));
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE proveedor SET activo = false WHERE id_proveedor = ?",
                "proveedor", "Baja lógica de proveedor id=" + id, id);
    }

    public Proveedor buscarPorId(int id) {
        return buscar("SELECT * FROM proveedor WHERE id_proveedor = ?", this::mapearProveedor, id);
    }

    public List<Proveedor> listarTodos() {
        return listar("SELECT * FROM proveedor ORDER BY nombre", this::mapearProveedor);
    }

    public List<Proveedor> listarActivas() {
        return listar("SELECT * FROM proveedor WHERE activo = true ORDER BY nombre", this::mapearProveedor);
    }
}
