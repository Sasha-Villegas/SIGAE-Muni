package sigae.muni.persistencia;

import sigae.muni.modelo.UmbralConfiguracion;
import sigae.muni.excepciones.EntidadNoEncontradaException;
import java.sql.*;

public class UmbralDAO {

    public UmbralConfiguracion buscarActivoPorEdificio(Long edificioId) throws SQLException {
        String sql = "SELECT * FROM umbrales_configuracion WHERE edificio_id = ? AND activo = TRUE";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, edificioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UmbralConfiguracion(
                            rs.getLong("id"),
                            rs.getDouble("porcentaje_desvio"),
                            rs.getInt("dias_historicos"),
                            rs.getDouble("ventana_temperatura"),
                            rs.getLong("edificio_id")
                    );
                }
            }
        }
        throw new EntidadNoEncontradaException("Umbral activo no encontrado para edificio " + edificioId);
    }
}
