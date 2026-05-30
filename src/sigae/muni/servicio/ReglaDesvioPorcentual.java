package sigae.muni.servicio;

import sigae.muni.modelo.Lectura;
import sigae.muni.modelo.UmbralConfiguracion;
import java.util.List;

public class ReglaDesvioPorcentual implements ReglaAlerta {

    @Override
    public boolean evaluar(Lectura actual, List<Lectura> historico, UmbralConfiguracion umbral) {
        double media = calcularMedia(historico);
        double limite = media * (1 + umbral.getPorcentajeDesvio() / 100.0);
        return actual.getConsumoKwh() > limite;
    }

    private double calcularMedia(List<Lectura> lecturas) {
        if (lecturas.isEmpty()) return 0.0;
        double suma = 0.0;
        for (Lectura l : lecturas) {
            suma += l.getConsumoKwh();
        }
        return suma / lecturas.size();
    }
}
