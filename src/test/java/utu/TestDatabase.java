package utu;

import utu.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Base de datos H2 en memoria (modo MySQL) para los tests.
 *
 * Debe invocarse desde @BeforeClass ANTES de usar cualquier DAO, porque
 * Conexion.conectar() lee los system properties la primera vez que conecta.
 *
 * Importante: los helpers abren su PROPIA conexión (nuevaConexion) en vez de
 * usar el singleton Conexion.conectar(), para no cerrarle la conexión compartida
 * a los DAOs bajo prueba.
 */
public final class TestDatabase {

    private static final String PERFIL_ADMIN = "Administrador";
    private static boolean esquemaCreado;

    private TestDatabase() {
    }

    /** Configura las propiedades de conexión y crea el esquema si aún no existe. */
    public static synchronized void setUp() {
        if (System.getProperty("hospital.db.url") == null) {
            System.setProperty("hospital.db.url",
                    "jdbc:h2:mem:hospital_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
            System.setProperty("hospital.db.user", "sa");
            System.setProperty("hospital.db.password", "");
        }
        if (esquemaCreado) {
            return;
        }
        try (Connection cn = nuevaConexion(); Statement st = cn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS perfil (
                        id_perfil INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(50) NOT NULL UNIQUE,
                        descripcion VARCHAR(150),
                        activo BOOLEAN DEFAULT TRUE
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS usuario (
                        id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(80) NOT NULL,
                        apellido VARCHAR(80) NOT NULL,
                        cedula VARCHAR(20) UNIQUE,
                        telefono VARCHAR(30),
                        correo VARCHAR(120),
                        usuario VARCHAR(40) UNIQUE NOT NULL,
                        contrasena VARCHAR(255) NOT NULL,
                        fecha_alta DATE,
                        ultimo_acceso DATE,
                        id_perfil INT NOT NULL,
                        activo BOOLEAN DEFAULT TRUE,
                        CONSTRAINT fk_usuario_perfil FOREIGN KEY (id_perfil) REFERENCES perfil (id_perfil)
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS auditoria (
                        id_auditoria INT AUTO_INCREMENT PRIMARY KEY,
                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        tabla_afectada VARCHAR(80),
                        operacion VARCHAR(30),
                        descripcion TEXT,
                        id_usuario INT,
                        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
                    )""");
            esquemaCreado = true;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo preparar la base de datos de prueba", e);
        }
    }

    /** Borra los datos de prueba y vuelve a crear el perfil base. */
    public static synchronized void limpiar() {
        setUp();
        try (Connection cn = nuevaConexion(); Statement st = cn.createStatement()) {
            st.executeUpdate("DELETE FROM auditoria");
            st.executeUpdate("DELETE FROM usuario");
            st.executeUpdate("DELETE FROM perfil");
            st.executeUpdate("INSERT INTO perfil (nombre, descripcion, activo) VALUES ('"
                    + PERFIL_ADMIN + "', 'Acceso total al sistema', TRUE)");
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo limpiar la base de datos de prueba", e);
        }
    }

    /** Conexión propia e independiente para los helpers, con los datos configurados. */
    private static Connection nuevaConexion() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty("hospital.db.url"),
                System.getProperty("hospital.db.user"),
                System.getProperty("hospital.db.password"));
    }

    /** Id del perfil usado por los usuarios de prueba. */
    public static int idPerfilAdmin() {
        return (int) valorEscalar("SELECT id_perfil FROM perfil WHERE nombre = '" + PERFIL_ADMIN + "' LIMIT 1");
    }

    /**
     * Inserta un usuario de prueba directamente en la base.
     *
     * @param contrasenaPlano se guarda hasheado con BCrypt, igual que hace la app
     * @return el id generado
     */
    public static int crearUsuario(String nombreUsuario, String contrasenaPlano, boolean activo) {
        String sql = """
                INSERT INTO usuario (nombre, apellido, cedula, telefono, correo, usuario, contrasena,
                                     fecha_alta, activo, id_perfil)
                VALUES ('Test', 'Prueba', NULL, NULL, NULL, ?, ?, CURRENT_DATE, ?, ?)""";
        try (Connection cn = nuevaConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, PasswordUtil.hash(contrasenaPlano));
            ps.setBoolean(3, activo);
            ps.setInt(4, idPerfilAdmin());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo crear el usuario de prueba '" + nombreUsuario + "'", e);
        }
    }

    /** Ejecuta un UPDATE/DELETE de prueba y devuelve la cantidad de filas afectadas. */
    public static int ejecutar(String sql) {
        try (Connection cn = nuevaConexion(); Statement st = cn.createStatement()) {
            return st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Falló la sentencia de prueba: " + sql, e);
        }
    }

    /** Primera columna/valor de una consulta de una sola fila, para aserciones. */
    public static Object valorEscalar(String sql) {
        try (Connection cn = nuevaConexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (!rs.next()) {
                return null;
            }
            return rs.getObject(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Fallo la consulta de prueba: " + sql, e);
        }
    }

    /** Valor DATE de una consulta como LocalDate, o null. */
    public static LocalDate valorFecha(String sql) {
        Object valor = valorEscalar(sql);
        return valor == null ? null : ((java.sql.Date) valor).toLocalDate();
    }
}
