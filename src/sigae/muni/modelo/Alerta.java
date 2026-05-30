package sigae.muni.modelo;

import java.time.LocalDateTime;

public class Alerta extends EntidadBase {
    public enum Estado { PENDIENTE, INVESTIGADA, DESCARTADA }
    private LocalDateTime fechaHora;
    private String tipo; // ANOMALIA_CONSUMO, UMBRAL_SUPERADO
    private double valorConsumo;
    private double umbralUtilizado;
    private Estado estado;
    private Long lecturaId;

    public Alerta(Long id, LocalDateTime fechaHora, String tipo, double valorConsumo, double umbralUtilizado, Long lecturaId) {
        setId(id);
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.valorConsumo = valorConsumo;
        this.umbralUtilizado = umbralUtilizado;
        this.estado = Estado.PENDIENTE;
        this.lecturaId = lecturaId;
    }

    // Getters y setters
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getValorConsumo() { return valorConsumo; }
    public void setValorConsumo(double valorConsumo) { this.valorConsumo = valorConsumo; }

    public double getUmbralUtilizado() { return umbralUtilizado; }
    public void setUmbralUtilizado(double umbralUtilizado) { this.umbralUtilizado = umbralUtilizado; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Long getLecturaId() { return lecturaId; }
    public void setLecturaId(Long lecturaId) { this.lecturaId = lecturaId; }
}