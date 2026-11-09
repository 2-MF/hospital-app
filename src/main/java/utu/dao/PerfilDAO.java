package utu.dao;

import utu.modelo.Perfil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PerfilDAO extends BaseDAO {

    private Perfil mapearPerfil(ResultSet rs) throws SQLException {
        Perfil perfil = new Perfil();
        perfil.setIdPerfil(rs.getInt("id_perfil"));
        perfil.setNombre(rs.getString("nombre"));
        perfil.setDescripcion(rs.getString("descripcion"));
        perfil.setActivo(rs.getBoolean("activo"));
        return perfil;
    }

    public boolean insertar(Perfil perfil) {
        int id = insertarAuditado("INSERT INTO perfil (nombre, descripcion, activo) VALUES (?, ?, ?)",
                "perfil", "Alta de perfil '" + perfil.getNombre() + "'",
                perfil.getNombre(), perfil.getDescripcion(), perfil.isActivo());
        if (id > 0) {
            perfil.setIdPerfil(id);
        }
        return id >= 0;
    }

    public boolean actualizar(Perfil perfil) {
        return actualizarAuditado("UPDATE perfil SET nombre = ?, descripcion = ?, activo = ? WHERE id_perfil = ?",
                "perfil", "Actualización de perfil '" + perfil.getNombre() + "'",
                perfil.getNombre(), perfil.getDescripcion(), perfil.isActivo(), perfil.getIdPerfil());
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE perfil SET activo = false WHERE id_perfil = ?",
                "perfil", "Baja lógica de perfil id=" + id, id);
    }

    public Perfil buscarPorId(int id) {
        return buscar("SELECT * FROM perfil WHERE id_perfil = ?", this::mapearPerfil, id);
    }

    public List<Perfil> listarTodos() {
        return listar("SELECT * FROM perfil ORDER BY nombre", this::mapearPerfil);
    }

    public List<Perfil> listarActivas() {
        return listar("SELECT * FROM perfil WHERE activo = true ORDER BY nombre", this::mapearPerfil);
    }

    public boolean existe(String nombre) {
        return existe(nombre, 0);
    }

    public boolean existe(String nombre, int idExcluir) {
        return hay("SELECT COUNT(*) FROM perfil WHERE UPPER(nombre) = UPPER(?) AND id_perfil <> ?", nombre, idExcluir);
    }
}
