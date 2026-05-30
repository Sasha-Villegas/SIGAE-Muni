package sigae.muni.persistencia;

import sigae.muni.modelo.Medidor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedidorDAO {

    public boolean existe(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medidores WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Medidor> listarTodos() throws SQLException {
        List<Medidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM medidores";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medidor m = new Medidor(
                        rs.getLong("id"),
                        rs.getString("numero_serie"),
                        rs.getString("ubicacion"),
                        rs.getLong("edificio_id")
                );
                lista.add(m);
            }
        }
        return lista;
    }
}