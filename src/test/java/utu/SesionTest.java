package utu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utu.modelo.Usuario;
import utu.util.Sesion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SesionTest {

    @Before
    @After
    public void reiniciarSesion() {
        Sesion.cerrar();
    }

    @Test
    public void sinLoginNoHayUsuario() {
        assertNull(Sesion.getUsuario());
        assertNull(Sesion.getIdUsuario());
    }

    @Test
    public void setUsuarioGuardaElUsuarioActual() {
        Usuario u = new Usuario();
        u.setIdUsuario(42);
        u.setUsuario("test_sesion");

        Sesion.setUsuario(u);

        assertSame(u, Sesion.getUsuario());
        assertEquals(Integer.valueOf(42), Sesion.getIdUsuario());
    }

    @Test
    public void cerrarLimpiaLaSesion() {
        Usuario u = new Usuario();
        u.setIdUsuario(7);
        Sesion.setUsuario(u);

        Sesion.cerrar();

        assertNull(Sesion.getUsuario());
        assertNull(Sesion.getIdUsuario());
    }

    @Test
    public void unNuevoLoginReemplazaAlUsuarioAnterior() {
        Usuario primero = new Usuario();
        primero.setIdUsuario(1);
        Sesion.setUsuario(primero);

        Usuario segundo = new Usuario();
        segundo.setIdUsuario(2);
        Sesion.setUsuario(segundo);

        assertSame(segundo, Sesion.getUsuario());
        assertEquals(Integer.valueOf(2), Sesion.getIdUsuario());
    }
}
