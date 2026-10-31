package utu;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utu.dao.LoginDAO;
import utu.modelo.Usuario;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LoginDAOTest {

    private static final String CLAVE = "claveSegura123";

    @BeforeClass
    public static void prepararBase() {
        TestDatabase.setUp();
    }

    @Before
    public void limpiarDatos() {
        TestDatabase.limpiar();
    }

    @Test
    public void loginValidoDevuelveElUsuarioConSuPerfil() {
        TestDatabase.crearUsuario("test_login", CLAVE, true);

        Usuario u = new LoginDAO().iniciarSesion("test_login", CLAVE);

        assertNotNull("Un usuario y contraseña válidos deben iniciar sesión", u);
        assertEquals("test_login", u.getUsuario());
        assertEquals("Test", u.getNombre());
        assertTrue(u.isActivo());
        assertNotNull(u.getPerfil());
        assertNotNull(u.getPerfil().getNombre());
    }

    @Test
    public void loginConUsuarioInexistenteDevuelveNull() {
        TestDatabase.crearUsuario("test_login", CLAVE, true);

        Usuario u = new LoginDAO().iniciarSesion("no_existe", CLAVE);

        assertNull(u);
    }

    @Test
    public void loginConContrasenaIncorrectaDevuelveNull() {
        TestDatabase.crearUsuario("test_login", CLAVE, true);

        Usuario u = new LoginDAO().iniciarSesion("test_login", "contrasenaEquivocada");

        assertNull(u);
    }

    @Test
    public void loginConUsuarioInactivoDevuelveNull() {
        TestDatabase.crearUsuario("test_inactivo", CLAVE, false);

        Usuario u = new LoginDAO().iniciarSesion("test_inactivo", CLAVE);

        assertNull("Un usuario dado de baja no debe poder iniciar sesión", u);
    }

    @Test
    public void loginConContrasenaVaciaDevuelveNull() {
        TestDatabase.crearUsuario("test_login", CLAVE, true);

        Usuario u = new LoginDAO().iniciarSesion("test_login", "");

        assertNull(u);
    }

    @Test
    public void loginConHashCorruptoDevuelveNullSinLanzarExcepcion() {
        // Simula un hash legacy o corrupto guardado en la base
        TestDatabase.crearUsuario("test_hash_roto", CLAVE, true);
        TestDatabase.ejecutar("UPDATE usuario SET contrasena = 'hash-corrupto' "
                + "WHERE usuario = 'test_hash_roto'");

        Usuario u = new LoginDAO().iniciarSesion("test_hash_roto", CLAVE);

        assertNull("Con hash inválido el login debe rechazarse, no fallar", u);
    }

    @Test
    public void loginExitosoActualizaElUltimoAccesoAHoy() {
        TestDatabase.crearUsuario("test_acceso", CLAVE, true);
        TestDatabase.ejecutar("UPDATE usuario SET ultimo_acceso = NULL "
                + "WHERE usuario = 'test_acceso'");

        new LoginDAO().iniciarSesion("test_acceso", CLAVE);

        LocalDate ultimoAcceso = TestDatabase.valorFecha(
                "SELECT ultimo_acceso FROM usuario WHERE usuario = 'test_acceso'");
        assertNotNull("El login debe registrar el último acceso", ultimoAcceso);
        assertEquals(LocalDate.now(), ultimoAcceso);
    }

    @Test
    public void loginFallidoNoActualizaElUltimoAcceso() {
        TestDatabase.crearUsuario("test_acceso", CLAVE, true);
        TestDatabase.ejecutar("UPDATE usuario SET ultimo_acceso = NULL "
                + "WHERE usuario = 'test_acceso'");

        new LoginDAO().iniciarSesion("test_acceso", "claveIncorrecta");

        LocalDate ultimoAcceso = TestDatabase.valorFecha(
                "SELECT ultimo_acceso FROM usuario WHERE usuario = 'test_acceso'");
        assertNull(ultimoAcceso);
    }
}
