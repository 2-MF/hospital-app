package utu.dao;

import utu.modelo.Marca;
import utu.modelo.Modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ModeloDAO extends BaseDAO {

    private static final String SELECT = "SELECT m.*, ma.nombre AS marca_nombre FROM modelo m "
            + "LEFT JOIN marca ma ON m.id_marca = ma.id_marca";

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

    private Object[] campos(Modelo m) {
        return new Object[]{m.getNombre(), m.getMarca().getIdMarca(), m.isActivo()};
    }

    public boolean insertar(Modelo modelo) {
        return insertarAuditado("INSERT INTO modelo (nombre, id_marca, activo) VALUES (?, ?, ?)",
                "modelo", "Alta de modelo '" + modelo.getNombre() + "'", campos(modelo)) >= 0;
    }

    public boolean actualizar(Modelo modelo) {
        return actualizarAuditado("UPDATE modelo SET nombre = ?, id_marca = ?, activo = ? WHERE id_modelo = ?",
                "modelo", "Actualización de modelo '" + modelo.getNombre() + "'",
                con(campos(modelo), modelo.getIdModelo()));
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE modelo SET activo = false WHERE id_modelo = ?",
                "modelo", "Baja lógica de modelo id=" + id, id);
    }

    public Modelo buscarPorId(int id) {
        return buscar(SELECT + " WHERE m.id_modelo = ?", this::mapearModelo, id);
    }

    public List<Modelo> listarTodos() {
        return listar(SELECT + " ORDER BY m.nombre", this::mapearModelo);
    }

    public List<Modelo> listarActivas() {
        return listar(SELECT + " WHERE m.activo = true ORDER BY m.nombre", this::mapearModelo);
    }
}
