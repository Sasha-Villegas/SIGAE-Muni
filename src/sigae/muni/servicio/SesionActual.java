package sigae.muni.servicio;

import sigae.muni.modelo.Usuario;

/**
 * SesionActual — patrón Singleton que representa la sesión activa.
 *
 * Guarda el usuario autenticado durante toda la ejecución de la aplicación.
 * Permite a cualquier controller consultar quién está logueado y con qué rol,
 * sin necesidad de pasar el objeto usuario entre pantallas.
 *
 * Uso:
 *   SesionActual.getInstance().iniciar(usuario);   // al loguearse
 *   SesionActual.getInstance().getUsuario();        // en cualquier controller
 *   SesionActual.getInstance().getRol();            // para control de permisos
 *   SesionActual.getInstance().cerrar();            // al salir
 */
public class SesionActual {

    // ─── Singleton ────────────────────────────────────────────────────────────
    private static SesionActual instancia;

    private SesionActual() {}

    public static SesionActual getInstance() {
        if (instancia == null) {
            instancia = new SesionActual();
        }
        return instancia;
    }

    // ─── Estado de sesión ─────────────────────────────────────────────────────
    private Usuario usuarioActual;

    public void iniciar(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrar() {
        this.usuarioActual = null;
    }

    public boolean estaActiva() {
        return usuarioActual != null;
    }

    public Usuario getUsuario() {
        return usuarioActual;
    }

    public String getNombre() {
        return usuarioActual != null ? usuarioActual.getNombre() : "Desconocido";
    }

    public String getRol() {
        return usuarioActual != null ? usuarioActual.getRol() : "";
    }

    // ─── Control de permisos por rol ──────────────────────────────────────────

    /**
     * Tabla de permisos por rol y sección:
     *
     * Sección    | ADMIN | OPERADOR | JEFE | SECRETARIO | AUDITOR
     * -----------|-------|----------|------|------------|--------
     * Dashboard  |  ✅   |    ✅    |  ✅  |     ✅     |   ✅
     * Edificios  |  ✅   |    ✅    |  ✅  |     ❌     |   ❌
     * Lecturas   |  ✅   |    ✅    |  ❌  |     ❌     |   ❌
     * Alertas    |  ✅   |    ✅    |  ✅  |     ❌     |   ✅
     * Reportes   |  ✅   |    ❌    |  ✅  |     ✅     |   ✅
     */
    public boolean puedeVerEdificios() {
        return tieneRol("ADMIN", "OPERADOR", "JEFE");
    }

    public boolean puedeVerLecturas() {
        return tieneRol("ADMIN", "OPERADOR");
    }

    public boolean puedeVerAlertas() {
        return tieneRol("ADMIN", "OPERADOR", "JEFE", "AUDITOR");
    }

    public boolean puedeVerReportes() {
        return tieneRol("ADMIN", "JEFE", "SECRETARIO", "AUDITOR");
    }

    private boolean tieneRol(String... roles) {
        String rolActual = getRol();
        for (String r : roles) {
            if (r.equalsIgnoreCase(rolActual)) return true;
        }
        return false;
    }
}
