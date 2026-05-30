package sigae.muni.servicio;

import sigae.muni.modelo.Lectura;
import sigae.muni.modelo.UmbralConfiguracion;
import java.util.List;

public interface ReglaAlerta {
    boolean evaluar(Lectura lecturaActual, List<Lectura> historico, UmbralConfiguracion umbral);
}