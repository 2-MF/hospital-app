package utu.dao;

import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionalidadDAO {

    /** Devuelve los nombres de las funcionalidades que tiene asignadas un perfil. */
    public List<String> nombresPorPerfil(int idPerfil) {
        String sql = """
                SELECT f.nombre
                FROM funcionalidad f
                INNER JOIN perfil_funcionalidad pf ON f.id_funcionalidad = pf.id_funcionalidad
                WHERE pf.id_perfil = ?
                ORDER BY f.nombre
                """;
        List<String> nombres = new ArrayList<>();
        try (PreparedStatement ps = Conexion.conectar().prepareStatement(sql)) {
            ps.setInt(1, idPerfil);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombres;
    }
}
