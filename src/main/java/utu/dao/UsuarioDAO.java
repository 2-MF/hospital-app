package utu.dao;

import utu.modelo.Perfil;
import utu.modelo.Usuario;
import utu.util.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsuarioDAO extends BaseDAO {

    static final String SELECT = "SELECT u.*, p.nombre AS perfil_nombre, p.descripcion AS perfil_descripcion, "
            + "p.activo AS perfil_activo FROM usuario u INNER JOIN perfil p ON u.id_perfil = p.id_perfil";

    static Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setCedula(rs.getString("cedula"));
        u.setTelefono(rs.getString("telefono"));
        u.setCorreo(rs.getString("correo"));
        u.setUsuario(rs.getString("usuario"));
        u.setFechaAlta(fecha(rs, "fecha_alta"));
        u.setUltimoAcceso(fecha(rs, "ultimo_acceso"));
        u.setActivo(rs.getBoolean("activo"));

        Perfil perfil = new Perfil();
        perfil.setIdPerfil(rs.getInt("id_perfil"));
        perfil.setNombre(rs.getString("perfil_nombre"));
        perfil.setDescripcion(rs.getString("perfil_descripcion"));
        perfil.setActivo(rs.getBoolean("perfil_activo"));
        u.setPerfil(perfil);

        return u;
    }

    private static boolean hayContrasena(Usuario u) {
        return u.getContrasena() != null && !u.getContrasena().isBlank();
    }

    public boolean insertar(Usuario u) {
        if (!hayContrasena(u)) {
            return false;
        }
        int id = insertarAuditado(
                "INSERT INTO usuario (nombre, apellido, cedula, telefono, correo, usuario, contrasena, fecha_alta, activo, id_perfil) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "usuario", "Alta de usuario '" + u.getUsuario() + "'",
                u.getNombre(), u.getApellido(), u.getCedula(), u.getTelefono(), u.getCorreo(), u.getUsuario(),
                PasswordUtil.hash(u.getContrasena()), LocalDate.now(), u.isActivo(), u.getPerfil().getIdPerfil());
        if (id > 0) {
            u.setIdUsuario(id);
        }
        return id >= 0;
    }

    public boolean actualizar(Usuario u) {
        boolean cambiarContrasena = hayContrasena(u);
        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, cedula = ?, telefono = ?, correo = ?, usuario = ?, "
                + (cambiarContrasena ? "contrasena = ?, " : "") + "activo = ?, id_perfil = ? WHERE id_usuario = ?";
        List<Object> params = new ArrayList<>(Arrays.asList(
                u.getNombre(), u.getApellido(), u.getCedula(), u.getTelefono(), u.getCorreo(), u.getUsuario()));
        if (cambiarContrasena) {
            params.add(PasswordUtil.hash(u.getContrasena()));
        }
        params.add(u.isActivo());
        params.add(u.getPerfil().getIdPerfil());
        params.add(u.getIdUsuario());
        return actualizarAuditado(sql, "usuario", "Actualización de usuario '" + u.getUsuario() + "'", params.toArray());
    }

    public boolean eliminar(int id) {
        return eliminarAuditado("UPDATE usuario SET activo = false WHERE id_usuario = ?",
                "usuario", "Baja lógica de usuario id=" + id, id);
    }

    public Usuario buscarPorId(int id) {
        return buscar(SELECT + " WHERE u.id_usuario = ?", UsuarioDAO::mapearUsuario, id);
    }

    public List<Usuario> listarTodos() {
        return listar(SELECT + " ORDER BY u.usuario", UsuarioDAO::mapearUsuario);
    }

    public boolean existeUsuario(String usuario) {
        return hay("SELECT COUNT(*) FROM usuario WHERE usuario = ?", usuario);
    }
}
