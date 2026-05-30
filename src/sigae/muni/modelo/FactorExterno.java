package sigae.muni.modelo;

import java.time.LocalDateTime;

public class FactorExterno extends EntidadBase {
    private LocalDateTime timestamp;
    private double temperaturaCelsius;
    private double humedadPorcentaje;
    private String fuente;

    public FactorExterno(Long id, LocalDateTime timestamp, double temperatura, double humedad, String fuente) {
        setId(id);
        this.timestamp = timestamp;
        this.temperaturaCelsius = temperatura;
        this.humedadPorcentaje = humedad;
        this.fuente = fuente;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public double getTemperaturaCelsius() { return temperaturaCelsius; }
    public void setTemperaturaCelsius(double temperaturaCelsius) { this.temperaturaCelsius = temperaturaCelsius; }

    public double getHumedadPorcentaje() { return humedadPorcentaje; }
    public void setHumedadPorcentaje(double humedadPorcentaje) { this.humedadPorcentaje = humedadPorcentaje; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }
}