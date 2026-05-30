package sigae.muni.modelo;

public class UmbralConfiguracion extends EntidadBase {
    private double porcentajeDesvio;
    private int diasHistoricos;
    private double ventanaTemperatura;
    private boolean activo;
    private Long edificioId;

    public UmbralConfiguracion(Long id, double porcentajeDesvio, int diasHistoricos, double ventanaTemperatura, Long edificioId) {
        setId(id);
        this.porcentajeDesvio = porcentajeDesvio;
        this.diasHistoricos = diasHistoricos;
        this.ventanaTemperatura = ventanaTemperatura;
        this.activo = true;
        this.edificioId = edificioId;
    }

    public double getPorcentajeDesvio() { return porcentajeDesvio; }
    public void setPorcentajeDesvio(double porcentajeDesvio) { this.porcentajeDesvio = porcentajeDesvio; }

    public int getDiasHistoricos() { return diasHistoricos; }
    public void setDiasHistoricos(int diasHistoricos) { this.diasHistoricos = diasHistoricos; }

    public double getVentanaTemperatura() { return ventanaTemperatura; }
    public void setVentanaTemperatura(double ventanaTemperatura) { this.ventanaTemperatura = ventanaTemperatura; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Long getEdificioId() { return edificioId; }
    public void setEdificioId(Long edificioId) { this.edificioId = edificioId; }
}