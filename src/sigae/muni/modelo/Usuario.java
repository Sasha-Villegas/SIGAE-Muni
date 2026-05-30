package sigae.muni.modelo;

public class Usuario extends EntidadBase {
    private String nombre;
    private String email;
    private String passwordHash; // En un entorno real almacenamos el hash
    private String rol; // ADMIN, OPERADOR, JEFE_MANTENIMIENTO, SECRETARIO, AUDITOR

    public Usuario(Long id, String nombre, String email, String passwordHash, String rol) {
        setId(id);
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }

    @Override
    public String toString() {
        return String.format("%s (%s)", nombre, rol);
    }
}