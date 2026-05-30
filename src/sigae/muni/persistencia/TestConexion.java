package sigae.muni.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConexion {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/sigae_muni";
        String user = "root";
        String pass = "Rocio0405."; // la que usaste en MySQL

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("¡Conexión exitosa a la base de datos!");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}
