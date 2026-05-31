package sigae.muni.persistencia;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("db.properties");
            props.load(fis);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Error: No se encontró el archivo db.properties.");
            System.err.println("Asegurate de crearlo en la raíz del proyecto con el siguiente contenido:");
            System.err.println("db.url=jdbc:mysql://localhost:3306/sigae_muni");
            System.err.println("db.user=root");
            System.err.println("db.password=TU_CONTRASEÑA");
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}