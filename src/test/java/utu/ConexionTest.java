package utu;

import org.junit.BeforeClass;
import org.junit.Test;
import utu.conexion.Conexion;

import java.lang.reflect.Field;
import java.sql.Connection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConexionTest {

    @BeforeClass
    public static void prepararBase() {
        TestDatabase.setUp();
    }

    @Test
    public void conectarDevuelveUnaConexionValida() {
        Connection cn = Conexion.conectar();
        assertNotNull(cn);
    }

    @Test
    public void conectarReutilizaLaMismaInstancia() {
        Connection primera = Conexion.conectar();
        Connection segunda = Conexion.conectar();
        assertSame("Debe ser una única conexión compartida (singleton)", primera, segunda);
    }

    @Test
    public void conectarLanzaRuntimeExceptionSiLaUrlEsInvalida() throws Exception {
        Field campo = Conexion.class.getDeclaredField("conexion");
        campo.setAccessible(true);
        Connection previa = (Connection) campo.get(null);
        if (previa != null) {
            previa.close(); // fuerza un nuevo intento de conexión
        }
        campo.set(null, null);

        String urlOriginal = System.getProperty("hospital.db.url");
        System.setProperty("hospital.db.url", "jdbc:h2:mem:inexistente;IFEXISTS=TRUE");
        try {
            Conexion.conectar();
            fail("Con una URL inválida debía lanzarse RuntimeException");
        } catch (RuntimeException esperado) {
            assertTrue(esperado.getMessage().startsWith("No se pudo conectar a la base de datos"));
        } finally {
            if (urlOriginal == null) {
                System.clearProperty("hospital.db.url");
            } else {
                System.setProperty("hospital.db.url", urlOriginal);
            }
            campo.set(null, null); // los próximos tests reconectan con la URL correcta
        }
    }
}
