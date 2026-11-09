package utu.dao;

import utu.modelo.Equipo;
import utu.modelo.Modelo;
import utu.modelo.Proveedor;
import utu.modelo.TipoEquipo;
import utu.modelo.Ubicacion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EquipoDAO extends BaseDAO {

    private static final String SELECT = "SELECT eq.*, mo.nombre AS modelo_nombre, te.nombre AS tipo_nombre, "
            + "pr.nombre AS proveedor_nombre, ub.nombre AS ubicacion_nombre FROM equipo eq "
            + "LEFT JOIN modelo mo ON eq.id_modelo = mo.id_modelo "
            + "LEFT JOIN tipo_equipo te ON eq.id_tipo = te.id_tipo "
            + "LEFT JOIN proveedor pr ON eq.id_proveedor = pr.id_proveedor "
            + "LEFT JOIN ubicacion ub ON eq.id_ubicacion = ub.id_ubicacion";

    private Equipo mapearEquipo(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombre(rs.getString("nombre"));
        e.setSerie(rs.getString("serie"));
        e.setFechaCompra(fecha(rs, "fecha_compra"));
        e.setFechaGarantia(fecha(rs, "fecha_garantia"));
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

    private Object[] campos(Equipo e) {
        return new Object[]{e.getCodigo(), e.getNombre(), e.getSerie(), e.getFechaCompra(), e.getFechaGarantia(),
                e.getEstado(), e.getObservaciones(), e.getModelo().getIdModelo(), e.getTipo().getIdTipo(),
                e.getProveedor().getIdProveedor(), e.getUbicacion().getIdUbicacion()};
    }

    public boolean insertar(Equipo e) {
        return insertarAuditado(
                "INSERT INTO equipo (codigo, nombre, serie, fecha_compra, fecha_garantia, estado, observaciones, id_modelo, id_tipo, id_proveedor, id_ubicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "equipo", "Alta de equipo '" + e.getCodigo() + "'", campos(e)) >= 0;
    }

    public boolean actualizar(Equipo e) {
        return actualizarAuditado(
                "UPDATE equipo SET codigo = ?, nombre = ?, serie = ?, fecha_compra = ?, fecha_garantia = ?, estado = ?, observaciones = ?, id_modelo = ?, id_tipo = ?, id_proveedor = ?, id_ubicacion = ? WHERE id_equipo = ?",
                "equipo", "Actualización de equipo '" + e.getCodigo() + "'", con(campos(e), e.getIdEquipo()));
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE equipo SET estado = 'INACTIVO' WHERE id_equipo = ?",
                "equipo", "Baja lógica de equipo id=" + id, id);
    }

    public Equipo buscarPorId(int id) {
        return buscar(SELECT + " WHERE eq.id_equipo = ?", this::mapearEquipo, id);
    }

    public List<Equipo> listarTodos() {
        return listar(SELECT + " ORDER BY eq.id_equipo", this::mapearEquipo);
    }

    public boolean existeCodigo(String codigo) {
        return hay("SELECT COUNT(*) FROM equipo WHERE codigo = ?", codigo);
    }
}
