package utu.dao;

import utu.modelo.Usuario;
import utu.util.PasswordUtil;

public class LoginDAO extends BaseDAO {

    public Usuario iniciarSesion(String usuario, String contrasena) {
        Usuario u = buscar(UsuarioDAO.SELECT + " WHERE u.usuario = ? AND u.activo = true", rs -> {
            Usuario encontrado = UsuarioDAO.mapearUsuario(rs);
            encontrado.setContrasena(rs.getString("contrasena"));
            return encontrado;
        }, usuario);

        if (u == null || !contrasenaValida(contrasena, u.getContrasena())) {
            return null;
        }
        u.setContrasena(null);

        ejecutar("UPDATE usuario SET ultimo_acceso = CURRENT_DATE WHERE id_usuario = ?", u.getIdUsuario());
        new AuditoriaDAO().registrar("usuario", "LOGIN",
                "Inicio de sesión del usuario '" + u.getUsuario() + "'", u.getIdUsuario());
        return u;
    }

    private static boolean contrasenaValida(String contrasena, String hash) {
        if (hash == null || hash.isBlank()) {
            return false;
        }
        try {
            return PasswordUtil.verificar(contrasena, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
