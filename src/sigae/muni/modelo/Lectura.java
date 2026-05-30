package sigae.muni.modelo;

import sigae.muni.excepciones.ValidacionException;
import java.time.LocalDateTime;

public class Lectura extends EntidadBase {
    private LocalDateTime timestamp;
    private double consumoKwh;
    private String tipoOrigen; // MANUAL o AUTOMATICA
    private Long medidorId;
    private FactorExterno factorExterno;

    public Lectura(Long id, LocalDateTime timestamp, double consumoKwh, String tipoOrigen, Long medidorId) {
        setId(id);
        this.timestamp = timestamp;
        setConsumoKwh(consumoKwh);
        this.tipoOrigen = tipoOrigen;
        this.medidorId = medidorId;
    }

    public double getConsumoKwh() { return consumoKwh; }
    public void setConsumoKwh(double consumoKwh) {
        if (consumoKwh < 0) {
            throw new ValidacionException("El consumo no puede ser negativo.");
        }
        this.consumoKwh = consumoKwh;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getTipoOrigen() { return tipoOrigen; }
    public void setTipoOrigen(String tipoOrigen) { this.tipoOrigen = tipoOrigen; }

    public Long getMedidorId() { return medidorId; }
    public void setMedidorId(Long medidorId) { this.medidorId = medidorId; }

    public FactorExterno getFactorExterno() { return factorExterno; }
    public void setFactorExterno(FactorExterno factorExterno) { this.factorExterno = factorExterno; }
}