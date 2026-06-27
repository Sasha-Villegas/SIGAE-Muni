package sigae.muni.servicio;

import sigae.muni.modelo.Usuario;
import sigae.muni.persistencia.UsuarioDAO;
import sigae.muni.excepciones.ValidacionException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * AuthService — servicio de autenticación de usuarios.
 *
 * Implementa autenticación con hash SHA-256, disponible en la
 * librería estándar de Java (java.security) sin dependencias externas.
 *
 * Decisión de diseño:
 *   El sistema completo utilizará bcrypt (vía Spring Security) en producción.
 *   En este prototipo se usa SHA-256 porque bcrypt requiere la librería
 *   org.mindrot:jbcrypt, externa al JDK. SHA-256 es suficiente para
 *   demostrar el flujo de autenticación en el contexto académico del TP.
 *
 * Para hashear contraseñas al crear usuarios en el seed.sql:
 *   SHA-256("admin123")   = "240be518..."  (calculado con este mismo método)
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Intenta autenticar al usuario con email y contraseña.
     *
     * @param email      Email ingresado en el formulario
     * @param password   Contraseña en texto plano ingresada en el formulario
     * @return           Usuario autenticado si las credenciales son correctas
     * @throws ValidacionException       Si email o password están vacíos
     * @throws AutenticacionException    Si el email no existe o la contraseña no coincide
     * @throws SQLException              Si hay un error de conexión con la BD
     */
    public Usuario autenticar(String email, String password)
            throws SQLException, ValidacionException, AutenticacionException {

        // Validación de campos vacíos
        if (email == null || email.trim().isEmpty()) {
            throw new ValidacionException("El email no puede estar vacío.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidacionException("La contraseña no puede estar vacía.");
        }

        // Buscar usuario en BD
        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim().toLowerCase());

        // Mensaje genérico: no revelar si el email existe o no (seguridad básica)
        if (usuario == null) {
            throw new AutenticacionException("Credenciales incorrectas.");
        }

        // Comparar hash de la contraseña ingresada con el hash almacenado
        String hashIngresado = hashearSHA256(password);
        if (!hashIngresado.equals(usuario.getPasswordHash())) {
            throw new AutenticacionException("Credenciales incorrectas.");
        }

        return usuario;
    }

    /**
     * Genera el hash SHA-256 de una cadena de texto.
     * Se usa para comparar la contraseña ingresada con el hash almacenado en BD.
     *
     * @param texto Texto a hashear (contraseña en texto plano)
     * @return      Hash SHA-256 en formato hexadecimal (64 caracteres)
     */
    public static String hashearSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convertir bytes a representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre está disponible en Java SE — nunca debería ocurrir
            throw new RuntimeException("Algoritmo SHA-256 no disponible.", e);
        }
    }

    /**
     * Excepción interna para credenciales inválidas.
     * Separada de ValidacionException para distinguir
     * "campo vacío" de "credenciales incorrectas".
     */
    public static class AutenticacionException extends Exception {
        public AutenticacionException(String mensaje) {
            super(mensaje);
        }
    }
}
