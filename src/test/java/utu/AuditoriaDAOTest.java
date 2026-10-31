package utu;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utu.dao.AuditoriaDAO;
import utu.modelo.Auditoria;
import utu.modelo.Usuario;
import utu.util.Sesion;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AuditoriaDAOTest {

    private AuditoriaDAO dao;

    @BeforeClass
    public static void prepararBase() {
        TestDatabase.setUp();
    }

    @Before
    public void limpiarDatos() {
        TestDatabase.limpiar();
        Sesion.cerrar();
        dao = new AuditoriaDAO();
    }

    @After
    public void cerrarSesion() {
        Sesion.cerrar();
    }

    private int crearUsuarioDePrueba(String nombreUsuario) {
        return TestDatabase.crearUsuario(nombreUsuario, "clave123", true);
    }

    @Test
    public void registrarEventoGuardaTablaOperacionYDescripcion() {
        int idUsuario = crearUsuarioDePrueba("test_auditoria");

        assertTrue(dao.registrar("usuario", "INSERT", "Alta de prueba", idUsuario));

        List<Auditoria> registros = dao.listarTodos();
        assertEquals(1, registros.size());
        Auditoria a = registros.get(0);
        assertEquals("usuario", a.getTablaAfectada());
        assertEquals("INSERT", a.getOperacion());
        assertEquals("Alta de prueba", a.getDescripcion());
        assertNotNull(a.getFecha());
        assertNotNull(a.getUsuario());
        assertEquals(idUsuario, a.getUsuario().getIdUsuario());
    }

    @Test
    public void registrarSinSesionGuardaElUsuarioComoNull() {
        Sesion.cerrar(); // nadie logueado

        assertTrue(dao.registrar("usuario", "DELETE", "Evento sin usuario"));

        Object idEnBase = TestDatabase.valorEscalar(
                "SELECT id_usuario FROM auditoria WHERE operacion = 'DELETE'");
        assertNull("Sin sesión el id de usuario debe quedar en NULL", idEnBase);

        Auditoria a = dao.listarTodos().get(0);
        assertNull(a.getUsuario());
    }

    @Test
    public void registrarUsaElUsuarioDeLaSesionActual() {
        int idUsuario = crearUsuarioDePrueba("test_sesion_aud");
        Usuario enSesion = new Usuario();
        enSesion.setIdUsuario(idUsuario);
        enSesion.setUsuario("test_sesion_aud");
        Sesion.setUsuario(enSesion);

        assertTrue(dao.registrar("usuario", "UPDATE", "Evento con sesión"));

        Auditoria a = dao.listarTodos().get(0);
        assertNotNull(a.getUsuario());
        assertEquals(idUsuario, a.getUsuario().getIdUsuario());
    }

    @Test
    public void registrarConIdUsuarioInexistenteNoRompePorLaForeignKey() {
        // id 0 => "sin usuario": el DAO debe guardar NULL, no un id que viola la FK
        assertTrue(dao.registrar("usuario", "INSERT", "Evento con id 0", 0));

        Object idEnBase = TestDatabase.valorEscalar(
                "SELECT id_usuario FROM auditoria WHERE descripcion = 'Evento con id 0'");
        assertNull(idEnBase);
    }

    @Test
    public void listarTodosOrdenaDelRegistroMasRecienteAlMasAntiguo() {
        crearUsuarioDePrueba("test_orden");
        dao.registrar("usuario", "INSERT", "primero"); // sin sesión => id_usuario NULL
        dao.registrar("usuario", "UPDATE", "segundo");

        List<Auditoria> registros = dao.listarTodos();

        assertTrue(registros.size() >= 2);
        assertEquals("El primer registro debe ser el más reciente", "segundo", registros.get(0).getDescripcion());
        assertEquals("primero", registros.get(1).getDescripcion());
    }
}
