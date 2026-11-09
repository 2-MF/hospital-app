package utu.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Connection conexion;

    public static Connection conectar() {
        String url = System.getProperty("hospital.db.url", "jdbc:mysql://localhost:3306/hospital");
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(url,
                        System.getProperty("hospital.db.user", "root"),
                        System.getProperty("hospital.db.password", ""));
            }
            return conexion;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a la base de datos (" + url + "): " + e.getMessage(), e);
        }
    }
}
