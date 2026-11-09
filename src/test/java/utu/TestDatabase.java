package utu;

import utu.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class TestDatabase {
    private static final String PERFIL_ADMIN = "Administrador";
    private static boolean esquemaCreado;

    private TestDatabase() {
    }

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
            st.execute("""
                    CREATE TABLE IF NOT EXISTS funcionalidad (
                        id_funcionalidad INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(80) NOT NULL,
                        descripcion VARCHAR(200)
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS perfil_funcionalidad (
                        id_perfil INT,
                        id_funcionalidad INT,
                        PRIMARY KEY (id_perfil, id_funcionalidad),
                        FOREIGN KEY (id_perfil) REFERENCES perfil (id_perfil),
                        FOREIGN KEY (id_funcionalidad) REFERENCES funcionalidad (id_funcionalidad)
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS pais (
                        id_pais INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(80) UNIQUE NOT NULL
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS institucion (
                        id_institucion INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(150),
                        direccion VARCHAR(200),
                        telefono VARCHAR(30),
                        id_pais INT NOT NULL,
                        activo BOOLEAN DEFAULT TRUE,
                        FOREIGN KEY (id_pais) REFERENCES pais (id_pais)
                    )""");
            esquemaCreado = true;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo preparar la base de datos de prueba", e);
        }
    }

    public static synchronized void limpiar() {
        setUp();
        try (Connection cn = nuevaConexion(); Statement st = cn.createStatement()) {
            st.executeUpdate("DELETE FROM auditoria");
            st.executeUpdate("DELETE FROM perfil_funcionalidad");
            st.executeUpdate("DELETE FROM funcionalidad");
            st.executeUpdate("DELETE FROM institucion");
            st.executeUpdate("DELETE FROM pais");
            st.executeUpdate("DELETE FROM usuario");
            st.executeUpdate("DELETE FROM perfil");
            st.executeUpdate("INSERT INTO perfil (nombre, descripcion, activo) VALUES ('"
                    + PERFIL_ADMIN + "', 'Acceso total al sistema', TRUE)");
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo limpiar la base de datos de prueba", e);
        }
    }

    private static Connection nuevaConexion() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty("hospital.db.url"),
                System.getProperty("hospital.db.user"),
                System.getProperty("hospital.db.password"));
    }

    public static int insertar(String sql) {
        try (Connection cn = nuevaConexion();
             Statement st = cn.createStatement()) {
            st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo insertar: " + sql, e);
        }
    }

    public static int idPerfilAdmin() {
        return (int) valorEscalar("SELECT id_perfil FROM perfil WHERE nombre = '" + PERFIL_ADMIN + "' LIMIT 1");
    }

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

    public static int ejecutar(String sql) {
        try (Connection cn = nuevaConexion(); Statement st = cn.createStatement()) {
            return st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Falló la sentencia de prueba: " + sql, e);
        }
    }

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

    public static LocalDate valorFecha(String sql) {
        Object valor = valorEscalar(sql);
        return valor == null ? null : ((java.sql.Date) valor).toLocalDate();
    }
}
