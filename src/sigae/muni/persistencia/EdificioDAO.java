package sigae.muni.persistencia;

import sigae.muni.modelo.Edificio;
import sigae.muni.excepciones.EntidadNoEncontradaException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EdificioDAO {

    public void insertar(Edificio e) throws SQLException {
        String sql = "INSERT INTO edificios (nombre, direccion, latitud, longitud, ocupacion_actual) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getDireccion());
            stmt.setDouble(3, e.getLatitud());
            stmt.setDouble(4, e.getLongitud());
            stmt.setString(5, e.getOcupacionActual());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getLong(1));
                }
            }
        }
    }

    public Edificio buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM edificios WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        throw new EntidadNoEncontradaException("Edificio no encontrado con ID: " + id);
    }

    public List<Edificio> listarTodos() throws SQLException {
        List<Edificio> lista = new ArrayList<>();
        String sql = "SELECT * FROM edificios";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void actualizar(Edificio e) throws SQLException {
        String sql = "UPDATE edificios SET nombre=?, direccion=?, latitud=?, longitud=?, ocupacion_actual=? WHERE id=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getDireccion());
            stmt.setDouble(3, e.getLatitud());
            stmt.setDouble(4, e.getLongitud());
            stmt.setString(5, e.getOcupacionActual());
            stmt.setLong(6, e.getId());
            stmt.executeUpdate();
        }
    }

    private Edificio mapear(ResultSet rs) throws SQLException {
        Edificio e = new Edificio(rs.getLong("id"), rs.getString("nombre"), rs.getString("direccion"));
        e.setLatitud(rs.getDouble("latitud"));
        e.setLongitud(rs.getDouble("longitud"));
        e.setOcupacionActual(rs.getString("ocupacion_actual"));
        return e;
    }
}