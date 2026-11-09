package utu.dao;

import utu.modelo.Marca;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MarcaDAO extends BaseDAO {

    private Marca mapearMarca(ResultSet rs) throws SQLException {
        Marca marca = new Marca();
        marca.setIdMarca(rs.getInt("id_marca"));
        marca.setNombre(rs.getString("nombre"));
        marca.setDescripcion(rs.getString("descripcion"));
        marca.setActivo(rs.getBoolean("activo"));
        return marca;
    }

    public boolean insertar(Marca marca) {
        return insertarAuditado("INSERT INTO marca (nombre, descripcion, activo) VALUES (?, ?, ?)",
                "marca", "Alta de marca '" + marca.getNombre() + "'",
                marca.getNombre(), marca.getDescripcion(), marca.isActivo()) >= 0;
    }

    public boolean actualizar(Marca marca) {
        return actualizarAuditado("UPDATE marca SET nombre = ?, descripcion = ?, activo = ? WHERE id_marca = ?",
                "marca", "Actualización de marca '" + marca.getNombre() + "'",
                marca.getNombre(), marca.getDescripcion(), marca.isActivo(), marca.getIdMarca());
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE marca SET activo = false WHERE id_marca = ?",
                "marca", "Baja lógica de marca id=" + id, id);
    }

    public Marca buscarPorId(int id) {
        return buscar("SELECT * FROM marca WHERE id_marca = ?", this::mapearMarca, id);
    }

    public List<Marca> listarTodos() {
        return listar("SELECT * FROM marca ORDER BY nombre", this::mapearMarca);
    }

    public List<Marca> listarActivas() {
        return listar("SELECT * FROM marca WHERE activo = true ORDER BY nombre", this::mapearMarca);
    }

    public boolean existe(String nombre) {
        return hay("SELECT COUNT(*) FROM marca WHERE UPPER(nombre) = UPPER(?)", nombre);
    }
}
