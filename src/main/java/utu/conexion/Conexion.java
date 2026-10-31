package utu.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL_DEFAULT = "jdbc:mysql://localhost:3306/hospital";
    private static final String USER_DEFAULT = "root";
    private static final String PASSWORD_DEFAULT = "";

    private static Connection conexion;

    /** Devuelve la conexión a la base de datos (una única para toda la aplicación). */
    public static Connection conectar() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Los system properties permiten apuntar a otra base (útil para los tests);
                // sin ellos se usan los valores por defecto de producción.
                String url = System.getProperty("hospital.db.url", URL_DEFAULT);
                String user = System.getProperty("hospital.db.user", USER_DEFAULT);
                String password = System.getProperty("hospital.db.password", PASSWORD_DEFAULT);
                conexion = DriverManager.getConnection(url, user, password);
            }
            return conexion;
        } catch (SQLException e) {
            String url = System.getProperty("hospital.db.url", URL_DEFAULT);
            throw new RuntimeException("No se pudo conectar a la base de datos (" + url + "): " + e.getMessage(), e);
        }
    }
}
