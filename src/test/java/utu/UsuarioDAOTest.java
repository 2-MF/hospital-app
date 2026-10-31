package utu;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utu.dao.LoginDAO;
import utu.dao.UsuarioDAO;
import utu.modelo.Perfil;
import utu.modelo.Usuario;
import utu.util.PasswordUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UsuarioDAOTest {

    private UsuarioDAO dao;

    @BeforeClass
    public static void prepararBase() {
        TestDatabase.setUp();
    }

    @Before
    public void limpiarDatos() {
        TestDatabase.limpiar();
        dao = new UsuarioDAO();
    }

    private Usuario nuevoUsuario(String nombreUsuario, String contrasena) {
        Usuario u = new Usuario();
        u.setNombre("Test");
        u.setApellido("Prueba");
        u.setUsuario(nombreUsuario);
        u.setContrasena(contrasena);
        u.setActivo(true);
        Perfil perfil = new Perfil();
        perfil.setIdPerfil(TestDatabase.idPerfilAdmin());
        u.setPerfil(perfil);
        return u;
    }

    /** Id real en la base del usuario de prueba indicado. */
    private int idDe(String nombreUsuario) {
        return ((Number) TestDatabase.valorEscalar(
                "SELECT id_usuario FROM usuario WHERE usuario = '" + nombreUsuario + "'")).intValue();
    }

    @Test
    public void insertarUsuarioQuedaPersistidoConHash() {
        Usuario u = nuevoUsuario("test_alta", "clave123");

        assertTrue("El alta de un usuario válido debe devolver true", dao.insertar(u));
        assertTrue(dao.existeUsuario("test_alta"));

        Usuario persistido = dao.buscarPorId(u.getIdUsuario());
        assertNotNull(persistido);
        assertEquals("test_alta", persistido.getUsuario());
        assertEquals("Test", persistido.getNombre());
        assertTrue(persistido.isActivo());
        assertNotNull("El alta asigna la fecha de alta del día", persistido.getFechaAlta());
        assertNotNull(persistido.getPerfil());

        String hash = String.valueOf(TestDatabase.valorEscalar(
                "SELECT contrasena FROM usuario WHERE usuario = 'test_alta'"));
        assertNotEquals("La contraseña no debe guardarse en texto plano", "clave123", hash);
        assertTrue("Debe guardarse un hash BCrypt verificable",
                PasswordUtil.verificar("clave123", hash));
    }

    @Test
    public void insertarSinContrasenaDevuelveFalse() {
        Usuario sinContrasena = nuevoUsuario("test_sin_clave", null);
        Usuario conContrasenaVacia = nuevoUsuario("test_clave_vacia", "");

        assertFalse("Sin contraseña el alta debe rechazarse", dao.insertar(sinContrasena));
        assertFalse("Con contraseña vacía el alta debe rechazarse", dao.insertar(conContrasenaVacia));
        assertFalse(dao.existeUsuario("test_sin_clave"));
        assertFalse(dao.existeUsuario("test_clave_vacia"));
    }

    @Test
    public void insertarUsuarioDuplicadoDevuelveFalse() {
        assertTrue(dao.insertar(nuevoUsuario("test_duplicado", "clave123")));

        assertFalse("El nombre de usuario es único, el segundo alta debe fallar",
                dao.insertar(nuevoUsuario("test_duplicado", "otraClave")));
    }

    @Test
    public void existeUsuarioDistingueExistenteDeInexistente() {
        assertFalse(dao.existeUsuario("fantasma"));
        dao.insertar(nuevoUsuario("real", "clave123"));
        assertTrue(dao.existeUsuario("real"));
    }

    @Test
    public void actualizarSinContrasenaMantieneElHashAnterior() {
        dao.insertar(nuevoUsuario("test_upd", "claveOriginal"));
        Usuario u = dao.buscarPorId(idDe("test_upd"));
        String hashOriginal = String.valueOf(TestDatabase.valorEscalar(
                "SELECT contrasena FROM usuario WHERE usuario = 'test_upd'"));

        u.setNombre("TestModificado");
        u.setContrasena(null); // no cambia la contraseña
        assertTrue(dao.actualizar(u));

        Usuario actualizado = dao.buscarPorId(u.getIdUsuario());
        assertEquals("TestModificado", actualizado.getNombre());
        String hashDespues = String.valueOf(TestDatabase.valorEscalar(
                "SELECT contrasena FROM usuario WHERE usuario = 'test_upd'"));
        assertEquals("Sin contraseña nueva el hash no debe cambiar", hashOriginal, hashDespues);
        assertTrue(PasswordUtil.verificar("claveOriginal", hashDespues));
    }

    @Test
    public void actualizarConNuevaContrasenaReemplazaElHash() {
        dao.insertar(nuevoUsuario("test_upd2", "claveVieja"));
        Usuario u = dao.buscarPorId(idDe("test_upd2"));

        u.setContrasena("claveNueva");
        assertTrue(dao.actualizar(u));

        String hash = String.valueOf(TestDatabase.valorEscalar(
                "SELECT contrasena FROM usuario WHERE usuario = 'test_upd2'"));
        assertTrue("La nueva contraseña debe verificarse contra el hash", PasswordUtil.verificar("claveNueva", hash));
        assertFalse("La contraseña anterior ya no debe funcionar", PasswordUtil.verificar("claveVieja", hash));
    }

    @Test
    public void eliminarEsBajaLogicaYBloqueaElLogin() {
        dao.insertar(nuevoUsuario("test_baja", "clave123"));

        assertTrue(dao.eliminar(idDe("test_baja")));

        Usuario enBase = dao.buscarPorId(idDe("test_baja"));
        assertNotNull("La baja es lógica: el registro sigue en la base", enBase);
        assertFalse(enBase.isActivo());

        assertNull("Un usuario de baja no puede iniciar sesión",
                new LoginDAO().iniciarSesion("test_baja", "clave123"));
    }

    @Test
    public void buscarPorIdInexistenteDevuelveNull() {
        assertNull(dao.buscarPorId(999999));
    }

    @Test
    public void listarTodosIncluyeAlUsuarioInsertado() {
        dao.insertar(nuevoUsuario("test_lista", "clave123"));

        List<Usuario> todos = dao.listarTodos();

        assertTrue(todos.size() >= 1);
        assertTrue(todos.stream().anyMatch(u -> "test_lista".equals(u.getUsuario())));
    }
}
