package utu.dao;

import utu.conexion.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseDAO {

    protected final Connection conexion = Conexion.conectar();

    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    protected <T> List<T> listar(String sql, Mapeador<T> mapeador, Object... params) {
        List<T> lista = new ArrayList<>();
        try (PreparedStatement ps = preparar(sql, Statement.NO_GENERATED_KEYS, params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapeador.mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    protected <T> T buscar(String sql, Mapeador<T> mapeador, Object... params) {
        List<T> lista = listar(sql, mapeador, params);
        return lista.isEmpty() ? null : lista.get(0);
    }

    protected Integer contar(String sql, Object... params) {
        try (PreparedStatement ps = preparar(sql, Statement.NO_GENERATED_KEYS, params);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean hay(String sql, Object... params) {
        Integer n = contar(sql, params);
        return n != null && n > 0;
    }

    protected boolean ejecutar(String sql, Object... params) {
        try (PreparedStatement ps = preparar(sql, Statement.NO_GENERATED_KEYS, params)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected int insertarAuditado(String sql, String tabla, String descripcion, Object... params) {
        try (PreparedStatement ps = preparar(sql, Statement.RETURN_GENERATED_KEYS, params)) {
            if (ps.executeUpdate() == 0) {
                return -1;
            }
            int id = 0;
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    id = claves.getInt(1);
                }
            }
            new AuditoriaDAO().registrar(tabla, "INSERT", descripcion);
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected boolean actualizarAuditado(String sql, String tabla, String descripcion, Object... params) {
        return ejecutarAuditado(sql, tabla, "UPDATE", descripcion, params);
    }

    protected boolean eliminarAuditado(String sql, String tabla, String descripcion, Object... params) {
        return ejecutarAuditado(sql, tabla, "DELETE", descripcion, params);
    }

    private boolean ejecutarAuditado(String sql, String tabla, String operacion, String descripcion, Object... params) {
        if (!ejecutar(sql, params)) {
            return false;
        }
        new AuditoriaDAO().registrar(tabla, operacion, descripcion);
        return true;
    }

    protected static Object[] con(Object[] params, Object ultimo) {
        Object[] todos = Arrays.copyOf(params, params.length + 1);
        todos[params.length] = ultimo;
        return todos;
    }

    protected static LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        Date d = rs.getDate(columna);
        return d == null ? null : d.toLocalDate();
    }

    private PreparedStatement preparar(String sql, int claves, Object[] params) throws SQLException {
        PreparedStatement ps = conexion.prepareStatement(sql, claves);
        try {
            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                ps.setObject(i + 1, p instanceof LocalDate d ? Date.valueOf(d) : p);
            }
        } catch (SQLException e) {
            ps.close();
            throw e;
        }
        return ps;
    }
}
