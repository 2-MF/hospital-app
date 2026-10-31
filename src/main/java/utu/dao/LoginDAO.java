package utu.dao;

import utu.modelo.Perfil;
import utu.modelo.Usuario;
import utu.util.PasswordUtil;
import utu.conexion.Conexion;
import java.sql.*;

public class LoginDAO {

    private final Connection conexion;

    public LoginDAO() {
        conexion = Conexion.conectar();
    }

    public Usuario iniciarSesion(String usuario, String contrasena) {
        String sql = """
                SELECT u.*, p.nombre AS perfil_nombre, p.descripcion AS perfil_descripcion, p.activo AS perfil_activo
                FROM usuario u
                INNER JOIN perfil p ON u.id_perfil = p.id_perfil
                WHERE u.usuario = ? AND u.activo = true
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Verificar la contraseña contra el hash guardado
                String hashGuardado = rs.getString("contrasena");
                boolean contrasenaValida = false;
                if (hashGuardado != null && !hashGuardado.isBlank()) {
                    try {
                        contrasenaValida = PasswordUtil.verificar(contrasena, hashGuardado);
                    } catch (IllegalArgumentException ex) {
                        contrasenaValida = false;
                    }
                }
                if (!contrasenaValida) {
                    return null;
                }

                Perfil perfil = new Perfil();
                perfil.setIdPerfil(rs.getInt("id_perfil"));
                perfil.setNombre(rs.getString("perfil_nombre"));
                perfil.setDescripcion(rs.getString("perfil_descripcion"));
                perfil.setActivo(rs.getBoolean("perfil_activo"));

                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCedula(rs.getString("cedula"));
                u.setTelefono(rs.getString("telefono"));
                u.setCorreo(rs.getString("correo"));
                u.setUsuario(rs.getString("usuario"));
                u.setActivo(rs.getBoolean("activo"));
                u.setPerfil(perfil);

                registrarAcceso(u.getIdUsuario());
                new AuditoriaDAO().registrar("usuario", "LOGIN",
                        "Inicio de sesión del usuario '" + u.getUsuario() + "'", u.getIdUsuario());

                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Actualiza la fecha de último acceso del usuario. */
    private void registrarAcceso(int idUsuario) {
        String sql = "UPDATE usuario SET ultimo_acceso = CURRENT_DATE WHERE id_usuario = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
