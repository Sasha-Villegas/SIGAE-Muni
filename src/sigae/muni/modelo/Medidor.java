package sigae.muni.modelo;

public class Medidor extends EntidadBase { // Herencia
    private String numeroSerie;
    private String tipo; // Siempre "ELECTRICO" en esta iteración
    private String ubicacion;
    private Long edificioId; // Relación con Edificio

    public Medidor(Long id, String numeroSerie, String ubicacion, Long edificioId) {
        setId(id);
        this.numeroSerie = numeroSerie;
        this.tipo = "ELECTRICO";
        this.ubicacion = ubicacion;
        this.edificioId = edificioId;
    }

    // Getters y setters (encapsulamiento)
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getTipo() { return tipo; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Long getEdificioId() { return edificioId; }
    public void setEdificioId(Long edificioId) { this.edificioId = edificioId; }
}