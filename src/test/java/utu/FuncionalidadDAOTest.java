package utu;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utu.dao.FuncionalidadDAO;
import utu.dao.PerfilDAO;
import utu.modelo.Funcionalidad;
import utu.modelo.Perfil;
import utu.util.Sesion;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FuncionalidadDAOTest {
    private FuncionalidadDAO dao;
    private int idPerfil;
    private int idUsuarios;
    private int idEquipos;
    private int idPaises;

    @BeforeClass
    public static void prepararBase() {
        TestDatabase.setUp();
    }

    @Before
    public void limpiarDatos() {
        TestDatabase.limpiar();
        Sesion.cerrar();
        dao = new FuncionalidadDAO();
        idPerfil = TestDatabase.idPerfilAdmin();
        idUsuarios = TestDatabase.insertar("INSERT INTO funcionalidad (nombre, descripcion) VALUES ('Usuarios', 'ABM')");
        idEquipos = TestDatabase.insertar("INSERT INTO funcionalidad (nombre, descripcion) VALUES ('Equipos', 'ABM')");
        idPaises = TestDatabase.insertar("INSERT INTO funcionalidad (nombre, descripcion) VALUES ('Países', 'ABM')");
    }

    @Test
    public void listarTodasDevuelveTodasEnOrdenDeCarga() {
        List<Funcionalidad> todas = dao.listarTodas();

        assertEquals(3, todas.size());
        assertEquals("Usuarios", todas.get(0).getNombre());
        assertEquals("Países", todas.get(2).getNombre());
        assertEquals(idPaises, todas.get(2).getIdFuncionalidad());
    }

    @Test
    public void asignarGuardaLasFuncionalidadesDelPerfil() {
        assertTrue(dao.asignar(idPerfil, List.of(idUsuarios, idPaises)));

        assertEquals(Set.of(idUsuarios, idPaises), dao.idsPorPerfil(idPerfil));

        assertEquals(List.of("Países", "Usuarios"), dao.nombresPorPerfil(idPerfil));
    }

    @Test
    public void asignarReemplazaLasAnteriores() {
        dao.asignar(idPerfil, List.of(idUsuarios, idEquipos));

        assertTrue(dao.asignar(idPerfil, List.of(idPaises)));

        assertEquals(Set.of(idPaises), dao.idsPorPerfil(idPerfil));
    }

    @Test
    public void asignarListaVaciaQuitaTodo() {
        dao.asignar(idPerfil, List.of(idUsuarios));

        assertTrue(dao.asignar(idPerfil, List.of()));

        assertTrue(dao.idsPorPerfil(idPerfil).isEmpty());
    }

    @Test
    public void asignarIgnoraIdsRepetidos() {
        assertTrue(dao.asignar(idPerfil, List.of(idEquipos, idEquipos)));

        assertEquals(Set.of(idEquipos), dao.idsPorPerfil(idPerfil));
    }

    @Test
    public void siFallaUnaInsercionSeConservanLosPermisosAnteriores() {
        dao.asignar(idPerfil, List.of(idUsuarios, idEquipos));

        assertFalse(dao.asignar(idPerfil, List.of(idPaises, 999999)));

        assertEquals(Set.of(idUsuarios, idEquipos), dao.idsPorPerfil(idPerfil));
    }

    @Test
    public void despuesDeUnFalloLaConexionSigueEnAutoCommit() throws Exception {
        dao.asignar(idPerfil, List.of(999999));

        assertTrue(utu.conexion.Conexion.conectar().getAutoCommit());

        assertTrue(dao.asignar(idPerfil, List.of(idUsuarios)));
        assertEquals(Set.of(idUsuarios), dao.idsPorPerfil(idPerfil));
    }

    @Test
    public void asignarRegistraAuditoria() {
        dao.asignar(idPerfil, List.of(idUsuarios, idEquipos));

        Object descripcion = TestDatabase.valorEscalar(
                "SELECT descripcion FROM auditoria WHERE tabla_afectada = 'perfil_funcionalidad' "
                        + "ORDER BY id_auditoria DESC LIMIT 1");
        assertEquals("Permisos del perfil id=" + idPerfil + ": 2 funcionalidad(es)", descripcion);
    }

    @Test
    public void perfilNuevoQuedaConSuIdParaAsignarPermisos() {
        Perfil perfil = new Perfil(0, "Técnico", "Mantenimiento", true);

        assertTrue(new PerfilDAO().insertar(perfil));
        assertTrue(perfil.getIdPerfil() > 0);

        assertTrue(dao.asignar(perfil.getIdPerfil(), List.of(idEquipos)));
        assertEquals(List.of("Equipos"), dao.nombresPorPerfil(perfil.getIdPerfil()));
    }

    @Test
    public void existePerfilExcluyendoElPropioId() {
        PerfilDAO perfiles = new PerfilDAO();
        Perfil perfil = new Perfil(0, "Auditor", null, true);
        perfiles.insertar(perfil);

        assertTrue(perfiles.existe("auditor"));
        assertFalse(perfiles.existe("AUDITOR", perfil.getIdPerfil()));
        assertTrue(perfiles.existe("Administrador", perfil.getIdPerfil()));
    }
}
