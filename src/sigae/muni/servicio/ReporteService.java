package sigae.muni.servicio;

import sigae.muni.modelo.Alerta;
import sigae.muni.modelo.Edificio;
import sigae.muni.persistencia.AlertaDAO;
import sigae.muni.servicio.EdificioService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ReporteService — servicio de generación de reportes del sistema.
 *
 * Demuestra el uso COMPLEMENTARIO de ArrayList y arreglos:
 *
 *   - ArrayList<Alerta>  : estructura dinámica para cargar datos desde la BD
 *                          (tamaño desconocido en tiempo de compilación)
 *   - String[][]         : arreglo estático bidimensional para estructurar
 *                          el reporte final (filas x columnas fijas)
 *   - String[]           : arreglo de encabezados (tamaño fijo conocido)
 *
 * El ordenamiento se implementa con burbuja sobre el arreglo bidimensional,
 * demostrando el uso de algoritmos de ordenación sobre arreglos nativos.
 */
public class ReporteService {

    private final AlertaDAO alertaDAO       = new AlertaDAO();
    private final EdificioService edificioService = new EdificioService();

    // ─── Encabezados fijos del reporte de alertas (arreglo estático) ──────────
    // Se usa String[] porque la cantidad de columnas es conocida y no cambia.
    public static final String[] ENCABEZADOS_ALERTAS = {
        "ID", "Fecha y hora", "Tipo", "Consumo (kWh)", "Umbral (%)", "Estado"
    };

    public static final String[] ENCABEZADOS_EDIFICIOS = {
        "ID", "Nombre", "Dirección", "Ocupación"
    };

    // ─── Reporte de alertas ───────────────────────────────────────────────────

    /**
     * Genera una matriz String[][] con los datos de alertas pendientes.
     *
     * Flujo:
     *  1. Carga alertas en ArrayList<Alerta> desde la BD (tamaño dinámico)
     *  2. Convierte al arreglo bidimensional String[][] (tamaño fijo)
     *  3. Ordena el arreglo por consumo descendente (burbuja)
     *
     * @return String[][] con filas de alertas ordenadas por consumo desc.
     */
    public String[][] generarMatrizAlertas() throws SQLException {
        // PASO 1 — ArrayList: carga dinámica desde BD
        // Se usa ArrayList porque no sabemos cuántas alertas hay antes de consultar
        ArrayList<Alerta> listaAlertas = new ArrayList<>();
        listaAlertas.addAll(alertaDAO.listarPendientes());

        if (listaAlertas.isEmpty()) {
            return new String[0][ENCABEZADOS_ALERTAS.length];
        }

        // PASO 2 — Arreglo bidimensional: estructura fija para el reporte
        // Ahora que sabemos el tamaño, creamos el arreglo estático
        int filas   = listaAlertas.size();
        int columnas = ENCABEZADOS_ALERTAS.length;
        String[][] matriz = new String[filas][columnas];

        for (int i = 0; i < filas; i++) {
            Alerta a = listaAlertas.get(i);
            matriz[i][0] = String.valueOf(a.getId());
            matriz[i][1] = a.getFechaHora().toString().replace("T", " ");
            matriz[i][2] = a.getTipo();
            matriz[i][3] = String.format("%.2f", a.getValorConsumo());
            matriz[i][4] = String.format("%.1f%%", a.getUmbralUtilizado());
            matriz[i][5] = a.getEstado().name();
        }

        // PASO 3 — Ordenamiento burbuja por consumo (columna 3) descendente
        ordenarPorConsumoDesc(matriz);

        return matriz;
    }

    /**
     * Genera una matriz String[][] con el resumen de edificios.
     */
    public String[][] generarMatrizEdificios() throws SQLException {
        // ArrayList: carga dinámica
        ArrayList<Edificio> listaEdificios = new ArrayList<>();
        listaEdificios.addAll(edificioService.listarTodos());

        if (listaEdificios.isEmpty()) {
            return new String[0][ENCABEZADOS_EDIFICIOS.length];
        }

        // Arreglo bidimensional: estructura fija
        String[][] matriz = new String[listaEdificios.size()][ENCABEZADOS_EDIFICIOS.length];

        for (int i = 0; i < listaEdificios.size(); i++) {
            Edificio e = listaEdificios.get(i);
            matriz[i][0] = String.valueOf(e.getId());
            matriz[i][1] = e.getNombre();
            matriz[i][2] = e.getDireccion();
            matriz[i][3] = e.getOcupacionActual();
        }

        // Ordenar alfabéticamente por nombre (columna 1)
        ordenarPorNombreAsc(matriz);

        return matriz;
    }

    /**
     * Retorna un resumen estadístico como arreglo simple de Strings.
     * Demuestra uso de arreglo unidimensional para datos de tamaño fijo.
     */
    public String[] generarResumenEstadistico() throws SQLException {
        ArrayList<Alerta> alertas   = new ArrayList<>(alertaDAO.listarPendientes());
        ArrayList<Edificio> edificios = new ArrayList<>(edificioService.listarTodos());

        // Calcular métricas sobre ArrayList
        double totalConsumo = 0;
        double maxConsumo   = 0;
        double minConsumo   = alertas.isEmpty() ? 0 : Double.MAX_VALUE;

        for (Alerta a : alertas) {
            double c = a.getValorConsumo();
            totalConsumo += c;
            if (c > maxConsumo) maxConsumo = c;
            if (c < minConsumo) minConsumo = c;
        }

        double promedio = alertas.isEmpty() ? 0 : totalConsumo / alertas.size();

        // Arreglo de tamaño fijo para el resumen (6 métricas conocidas)
        String[] resumen = new String[6];
        resumen[0] = "Total edificios: "        + edificios.size();
        resumen[1] = "Total alertas pendientes: " + alertas.size();
        resumen[2] = "Consumo promedio alertas: " + String.format("%.2f kWh", promedio);
        resumen[3] = "Consumo máximo: "           + String.format("%.2f kWh", maxConsumo);
        resumen[4] = "Consumo mínimo: "           + String.format("%.2f kWh", minConsumo);
        resumen[5] = "Consumo total alertas: "    + String.format("%.2f kWh", totalConsumo);

        return resumen;
    }

    // ─── Algoritmos de ordenamiento sobre arreglos ────────────────────────────

    /**
     * Ordenamiento burbuja sobre String[][] por columna 3 (consumo) descendente.
     * Se trabaja directamente sobre el arreglo nativo para demostrar
     * manipulación de arreglos bidimensionales.
     */
    private void ordenarPorConsumoDesc(String[][] matriz) {
        int n = matriz.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double consumoActual   = parsearConsumo(matriz[j][3]);
                double consumoSiguiente = parsearConsumo(matriz[j + 1][3]);
                if (consumoActual < consumoSiguiente) {
                    // Intercambiar filas completas
                    String[] temp   = matriz[j];
                    matriz[j]       = matriz[j + 1];
                    matriz[j + 1]   = temp;
                }
            }
        }
    }

    /**
     * Ordenamiento burbuja sobre String[][] por columna 1 (nombre) ascendente.
     */
    private void ordenarPorNombreAsc(String[][] matriz) {
        int n = matriz.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (matriz[j][1].compareToIgnoreCase(matriz[j + 1][1]) > 0) {
                    String[] temp   = matriz[j];
                    matriz[j]       = matriz[j + 1];
                    matriz[j + 1]   = temp;
                }
            }
        }
    }

    private double parsearConsumo(String valor) {
        try {
            return Double.parseDouble(valor.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
