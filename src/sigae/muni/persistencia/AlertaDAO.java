package sigae.muni.persistencia;

import sigae.muni.modelo.Alerta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaDAO {

    public void insertar(Alerta a) throws SQLException {
        String sql = "INSERT INTO alertas (fecha_hora, tipo, valor_consumo, umbral_utilizado, estado, lectura_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(a.getFechaHora()));
            stmt.setString(2, a.getTipo());
            stmt.setDouble(3, a.getValorConsumo());
            stmt.setDouble(4, a.getUmbralUtilizado());
            stmt.setString(5, a.getEstado().name());
            stmt.setLong(6, a.getLecturaId());
            stmt.executeUpdate();
        }
    }

    public List<Alerta> listarPendientes() throws SQLException {
        List<Alerta> lista = new ArrayList<>();
        String sql = "SELECT * FROM alertas WHERE estado = 'PENDIENTE'";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Alerta a = new Alerta(
                        rs.getLong("id"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime(),
                        rs.getString("tipo"),
                        rs.getDouble("valor_consumo"),
                        rs.getDouble("umbral_utilizado"),
                        rs.getLong("lectura_id")
                );
                a.setEstado(Alerta.Estado.valueOf(rs.getString("estado")));
                lista.add(a);
            }
        }
        return lista;
    }
}
