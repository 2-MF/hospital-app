package utu.dao;

import utu.modelo.Funcionalidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FuncionalidadDAO extends BaseDAO {

    public List<String> nombresPorPerfil(int idPerfil) {
        return listar("SELECT f.nombre FROM funcionalidad f "
                + "INNER JOIN perfil_funcionalidad pf ON f.id_funcionalidad = pf.id_funcionalidad "
                + "WHERE pf.id_perfil = ? ORDER BY f.nombre", rs -> rs.getString("nombre"), idPerfil);
    }

    public List<Funcionalidad> listarTodas() {
        return listar("SELECT id_funcionalidad, nombre, descripcion FROM funcionalidad ORDER BY id_funcionalidad",
                rs -> new Funcionalidad(rs.getInt("id_funcionalidad"), rs.getString("nombre"), rs.getString("descripcion")));
    }

    public Set<Integer> idsPorPerfil(int idPerfil) {
        return new LinkedHashSet<>(listar("SELECT id_funcionalidad FROM perfil_funcionalidad WHERE id_perfil = ?",
                rs -> rs.getInt(1), idPerfil));
    }

    public boolean asignar(int idPerfil, Collection<Integer> idsFuncionalidad) {
        Set<Integer> ids = new LinkedHashSet<>(idsFuncionalidad);
        Connection cn = conexion;
        try {
            cn.setAutoCommit(false);
            try (PreparedStatement borrar = cn.prepareStatement("DELETE FROM perfil_funcionalidad WHERE id_perfil = ?");
                 PreparedStatement insertar = cn.prepareStatement(
                         "INSERT INTO perfil_funcionalidad (id_perfil, id_funcionalidad) VALUES (?, ?)")) {
                borrar.setInt(1, idPerfil);
                borrar.executeUpdate();
                for (Integer id : ids) {
                    insertar.setInt(1, idPerfil);
                    insertar.setInt(2, id);
                    insertar.addBatch();
                }
                insertar.executeBatch();
            }
            cn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                cn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        new AuditoriaDAO().registrar("perfil_funcionalidad", "UPDATE",
                "Permisos del perfil id=" + idPerfil + ": " + ids.size() + " funcionalidad(es)");
        return true;
    }
}
