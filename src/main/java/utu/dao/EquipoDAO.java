package utu.dao;

import utu.modelo.Equipo;
import utu.modelo.Modelo;
import utu.modelo.Proveedor;
import utu.modelo.TipoEquipo;
import utu.modelo.Ubicacion;
import utu.conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    private final Connection conexion;

    public EquipoDAO() {
        conexion = Conexion.conectar();
    }

    private Equipo mapearEquipo(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombre(rs.getString("nombre"));
        e.setSerie(rs.getString("serie"));

        java.sql.Date fechaCompra = rs.getDate("fecha_compra");
        if (fechaCompra != null) {
            e.setFechaCompra(fechaCompra.toLocalDate());
        }

        java.sql.Date fechaGarantia = rs.getDate("fecha_garantia");
        if (fechaGarantia != null) {
            e.setFechaGarantia(fechaGarantia.toLocalDate());
        }

        e.setEstado(rs.getString("estado"));
        e.setObservaciones(rs.getString("observaciones"));

        Modelo modelo = new Modelo();
        modelo.setIdModelo(rs.getInt("id_modelo"));
        modelo.setNombre(rs.getString("modelo_nombre"));
        e.setModelo(modelo);

        TipoEquipo tipo = new TipoEquipo();
        tipo.setIdTipo(rs.getInt("id_tipo"));
        tipo.setNombre(rs.getString("tipo_nombre"));
        e.setTipo(tipo);

        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(rs.getInt("id_proveedor"));
        proveedor.setNombre(rs.getString("proveedor_nombre"));
        e.setProveedor(proveedor);

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setIdUbicacion(rs.getInt("id_ubicacion"));
        ubicacion.setNombre(rs.getString("ubicacion_nombre"));
        e.setUbicacion(ubicacion);

        return e;
    }

    public boolean insertar(Equipo e) {
        String sql = "INSERT INTO equipo (codigo, nombre, serie, fecha_compra, fecha_garantia, estado, observaciones, id_modelo, id_tipo, id_proveedor, id_ubicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getSerie());
            if (e.getFechaCompra() == null) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setDate(4, java.sql.Date.valueOf(e.getFechaCompra()));
            }
            if (e.getFechaGarantia() == null) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(5, java.sql.Date.valueOf(e.getFechaGarantia()));
            }
            ps.setString(6, e.getEstado());
            ps.setString(7, e.getObservaciones());
            ps.setInt(8, e.getModelo().getIdModelo());
            ps.setInt(9, e.getTipo().getIdTipo());
            ps.setInt(10, e.getProveedor().getIdProveedor());
            ps.setInt(11, e.getUbicacion().getIdUbicacion());

            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("equipo", "INSERT", "Alta de equipo '" + e.getCodigo() + "'");
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipo SET codigo = ?, nombre = ?, serie = ?, fecha_compra = ?, fecha_garantia = ?, estado = ?, observaciones = ?, id_modelo = ?, id_tipo = ?, id_proveedor = ?, id_ubicacion = ? WHERE id_equipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getSerie());
            if (e.getFechaCompra() == null) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setDate(4, java.sql.Date.valueOf(e.getFechaCompra()));
            }
            if (e.getFechaGarantia() == null) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(5, java.sql.Date.valueOf(e.getFechaGarantia()));
            }
            ps.setString(6, e.getEstado());
            ps.setString(7, e.getObservaciones());
            ps.setInt(8, e.getModelo().getIdModelo());
            ps.setInt(9, e.getTipo().getIdTipo());
            ps.setInt(10, e.getProveedor().getIdProveedor());
            ps.setInt(11, e.getUbicacion().getIdUbicacion());
            ps.setInt(12, e.getIdEquipo());

            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("equipo", "UPDATE", "Actualización de equipo '" + e.getCodigo() + "'");
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE equipo SET estado = 'INACTIVO' WHERE id_equipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                new AuditoriaDAO().registrar("equipo", "DELETE", "Baja lógica de equipo id=" + id);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Equipo buscarPorId(int id) {
        String sql = "SELECT eq.*, mo.nombre AS modelo_nombre, te.nombre AS tipo_nombre, pr.nombre AS proveedor_nombre, ub.nombre AS ubicacion_nombre FROM equipo eq LEFT JOIN modelo mo ON eq.id_modelo = mo.id_modelo LEFT JOIN tipo_equipo te ON eq.id_tipo = te.id_tipo LEFT JOIN proveedor pr ON eq.id_proveedor = pr.id_proveedor LEFT JOIN ubicacion ub ON eq.id_ubicacion = ub.id_ubicacion WHERE eq.id_equipo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearEquipo(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Equipo> listarTodos() {
        String sql = "SELECT eq.*, mo.nombre AS modelo_nombre, te.nombre AS tipo_nombre, pr.nombre AS proveedor_nombre, ub.nombre AS ubicacion_nombre FROM equipo eq LEFT JOIN modelo mo ON eq.id_modelo = mo.id_modelo LEFT JOIN tipo_equipo te ON eq.id_tipo = te.id_tipo LEFT JOIN proveedor pr ON eq.id_proveedor = pr.id_proveedor LEFT JOIN ubicacion ub ON eq.id_ubicacion = ub.id_ubicacion ORDER BY eq.id_equipo";
        List<Equipo> equipos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                equipos.add(mapearEquipo(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return equipos;
    }

    public boolean existeCodigo(String codigo) {
        String sql = "SELECT COUNT(*) FROM equipo WHERE codigo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
