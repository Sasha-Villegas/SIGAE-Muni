package sigae.muni.persistencia;

import sigae.muni.modelo.Lectura;
import sigae.muni.modelo.FactorExterno;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LecturaDAO {

    public void insertar(Lectura l, Long factorExternoId) throws SQLException {
        String sql = "INSERT INTO lecturas (timestamp, consumo_kwh, tipo_origen, medidor_id, factor_externo_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(l.getTimestamp()));
            stmt.setDouble(2, l.getConsumoKwh());
            stmt.setString(3, l.getTipoOrigen());
            stmt.setLong(4, l.getMedidorId());
            if (factorExternoId != null) stmt.setLong(5, factorExternoId);
            else stmt.setNull(5, Types.BIGINT);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) l.setId(keys.getLong(1));
            }
        }
    }

    public List<Lectura> listarPorMedidor(Long medidorId) throws SQLException {
        List<Lectura> lista = new ArrayList<>();
        String sql = "SELECT * FROM lecturas WHERE medidor_id = ? ORDER BY timestamp DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, medidorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Lectura mapear(ResultSet rs) throws SQLException {
        Lectura l = new Lectura(
                rs.getLong("id"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getDouble("consumo_kwh"),
                rs.getString("tipo_origen"),
                rs.getLong("medidor_id")
        );
        // FactorExterno se carga si es necesario, aquí no para simplificar
        return l;
    }
}