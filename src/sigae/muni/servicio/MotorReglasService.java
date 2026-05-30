package sigae.muni.servicio;

import sigae.muni.modelo.Lectura;
import sigae.muni.modelo.UmbralConfiguracion;
import sigae.muni.excepciones.DatosInsuficientesException;
import java.util.List;

public class MotorReglasService {
    private ReglaAlerta regla;

    public MotorReglasService() {
        this.regla = new ReglaDesvioPorcentual(); // Polimorfismo: se puede cambiar en tiempo de ejecución
    }

    public void cambiarRegla(ReglaAlerta nueva) {
        this.regla = nueva;
    }

    public boolean evaluarLectura(Lectura lectura, List<Lectura> historico, UmbralConfiguracion umbral) {
        if (historico.size() < umbral.getDiasHistoricos()) {
            throw new DatosInsuficientesException("Se necesitan al menos " + umbral.getDiasHistoricos() + " lecturas.");
        }
        return regla.evaluar(lectura, historico, umbral);
    }
}