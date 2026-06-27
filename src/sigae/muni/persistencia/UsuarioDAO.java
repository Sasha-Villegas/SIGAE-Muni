package sigae.muni.persistencia;

import sigae.muni.modelo.Usuario;
import java.sql.*;

/**
 * UsuarioDAO — acceso a datos de la tabla usuarios.
 * Provee búsqueda por email para el proceso de autenticación.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por su email.
     * Retorna null si no existe.
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("rol")
                    );
                }
            }
        }
        return null;
    }
}
