package sigae.muni.persistencia;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ConexionBD — gestión de conexiones con HikariCP connection pool.
 * En lugar de abrir una conexión nueva por cada query,
 * HikariCP mantiene un pool de conexiones reutilizables,
 * reduciendo significativamente la latencia en BD remotas.
 */
public class ConexionBD {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("db.properties");
            props.load(fis);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));

            // Pool de conexiones
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            // Optimizaciones para BD remota
            config.addDataSourceProperty("cachePrepStmts",          "true");
            config.addDataSourceProperty("prepStmtCacheSize",        "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit",    "2048");
            config.addDataSourceProperty("useServerPrepStmts",       "true");
            config.addDataSourceProperty("rewriteBatchedStatements",  "true");

            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            System.err.println("Error: No se encontró db.properties");
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}