package utu;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utu.dao.PaisDAO;
import utu.modelo.Pais;
import utu.util.Sesion;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PaisDAOTest {
    private PaisDAO dao;

    @BeforeClass
    public static void prepararBase() {
        TestDatabase.setUp();
    }

    @Before
    public void limpiarDatos() {
        TestDatabase.limpiar();
        Sesion.cerrar();
        dao = new PaisDAO();
    }

    private int idDe(String nombre) {
        return ((Number) TestDatabase.valorEscalar("SELECT id_pais FROM pais WHERE nombre = '" + nombre + "'")).intValue();
    }

    @Test
    public void insertarYListarOrdenadoPorNombre() {
        assertTrue(dao.insertar(new Pais(0, "Uruguay")));
        assertTrue(dao.insertar(new Pais(0, "Argentina")));

        List<Pais> paises = dao.listarTodos();

        assertEquals(2, paises.size());
        assertEquals("Argentina", paises.get(0).getNombre());
        assertEquals("Uruguay", paises.get(1).getNombre());
    }

    @Test
    public void insertarRegistraAuditoria() {
        dao.insertar(new Pais(0, "Chile"));

        Object operacion = TestDatabase.valorEscalar(
                "SELECT operacion FROM auditoria WHERE tabla_afectada = 'pais' ORDER BY id_auditoria DESC LIMIT 1");
        assertEquals("INSERT", operacion);
    }

    @Test
    public void nombreRepetidoNoSeInserta() {
        assertTrue(dao.insertar(new Pais(0, "Brasil")));
        assertFalse(dao.insertar(new Pais(0, "Brasil")));
    }

    @Test
    public void existeIgnoraMayusculasYExcluyeElPropioId() {
        dao.insertar(new Pais(0, "Paraguay"));
        int id = idDe("Paraguay");

        assertTrue(dao.existe("PARAGUAY", 0));

        assertFalse(dao.existe("paraguay", id));
        assertFalse(dao.existe("Bolivia", 0));
    }

    @Test
    public void actualizarCambiaElNombre() {
        dao.insertar(new Pais(0, "Urugay"));
        int id = idDe("Urugay");

        assertTrue(dao.actualizar(new Pais(id, "Uruguay")));
        assertEquals("Uruguay", dao.buscarPorId(id).getNombre());
    }

    @Test
    public void eliminarPaisSinInstituciones() {
        dao.insertar(new Pais(0, "Perú"));
        int id = idDe("Perú");

        assertFalse(dao.tieneInstituciones(id));
        assertTrue(dao.eliminar(id));
        assertNull(dao.buscarPorId(id));
    }

    @Test
    public void paisConInstitucionesNoSePuedeEliminar() {
        dao.insertar(new Pais(0, "Uruguay"));
        int id = idDe("Uruguay");
        TestDatabase.insertar("INSERT INTO institucion (nombre, id_pais) VALUES ('Hospital de prueba', " + id + ")");

        assertTrue(dao.tieneInstituciones(id));

        assertFalse(dao.eliminar(id));
        assertEquals("Uruguay", dao.buscarPorId(id).getNombre());
    }
}
