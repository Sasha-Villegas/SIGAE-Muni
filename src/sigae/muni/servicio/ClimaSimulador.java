package sigae.muni.servicio;

import java.util.Random;

public class ClimaSimulador {
    private static Random random = new Random();

    public static double obtenerTemperatura() {
        return 15.0 + random.nextDouble() * 15.0; // Entre 15 y 30 grados
    }
}