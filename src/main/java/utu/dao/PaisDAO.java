package utu.dao;

import utu.modelo.Pais;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PaisDAO extends BaseDAO {

    private Pais mapearPais(ResultSet rs) throws SQLException {
        Pais pais = new Pais();
        pais.setIdPais(rs.getInt("id_pais"));
        pais.setNombre(rs.getString("nombre"));
        return pais;
    }

    public boolean insertar(Pais pais) {
        return insertarAuditado("INSERT INTO pais (nombre) VALUES (?)",
                "pais", "Alta de país '" + pais.getNombre() + "'", pais.getNombre()) >= 0;
    }

    public boolean actualizar(Pais pais) {
        return actualizarAuditado("UPDATE pais SET nombre = ? WHERE id_pais = ?",
                "pais", "Actualización de país '" + pais.getNombre() + "'", pais.getNombre(), pais.getIdPais());
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("DELETE FROM pais WHERE id_pais = ?",
                "pais", "Eliminación de país id=" + id, id);
    }

    public Pais buscarPorId(int id) {
        return buscar("SELECT * FROM pais WHERE id_pais = ?", this::mapearPais, id);
    }

    public List<Pais> listarTodos() {
        return listar("SELECT * FROM pais ORDER BY nombre", this::mapearPais);
    }

    public boolean existe(String nombre, int idExcluir) {
        return hay("SELECT COUNT(*) FROM pais WHERE UPPER(nombre) = UPPER(?) AND id_pais <> ?", nombre, idExcluir);
    }

    public boolean tieneInstituciones(int idPais) {
        Integer cantidad = contar("SELECT COUNT(*) FROM institucion WHERE id_pais = ?", idPais);
        return cantidad == null || cantidad > 0;
    }
}
