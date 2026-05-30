package sigae.muni.modelo;

// Abstracción: clase abstracta que define la estructura común a todas las entidades
public abstract class EntidadBase {
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}