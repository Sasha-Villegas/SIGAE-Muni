package sigae.muni.modelo;

import sigae.muni.excepciones.ValidacionException;

public class Edificio extends EntidadBase { // Herencia
    private String nombre;
    private String direccion;
    private double latitud;
    private double longitud;
    private String ocupacionActual; // BAJA, MEDIA, ALTA

    // Constructor para inicializar objetos
    public Edificio(Long id, String nombre, String direccion) {
        setId(id);
        setNombre(nombre);
        setDireccion(direccion);
        this.ocupacionActual = "MEDIA"; // valor por defecto
    }

    // Encapsulamiento: atributos privados con getters y setters públicos
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre del edificio no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getOcupacionActual() { return ocupacionActual; }
    public void setOcupacionActual(String ocupacionActual) {
        if (!ocupacionActual.equals("BAJA") && !ocupacionActual.equals("MEDIA") && !ocupacionActual.equals("ALTA")) {
            throw new ValidacionException("Ocupación inválida. Debe ser BAJA, MEDIA o ALTA.");
        }
        this.ocupacionActual = ocupacionActual;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", getId(), nombre, direccion);
    }
}
