package utu.util;

import utu.modelo.Usuario;

public class Sesion {
    private static Usuario usuarioActual;

    public static void setUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static Integer getIdUsuario() {
        return usuarioActual == null ? null : usuarioActual.getIdUsuario();
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}
