package sigae.muni.servicio;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ExportadorArchivo — módulo de persistencia en archivos del sistema SIGAE-Muni.
 *
 * Permite exportar datos del sistema a archivos .txt y .csv,
 * y recuperar información previamente guardada.
 *
 * Implementa dos tipos de persistencia complementaria:
 *   - Base de datos MySQL : datos transaccionales en tiempo real
 *   - Archivos            : reportes exportables y log de operaciones
 *
 * Todos los métodos manejan excepciones de IO de forma controlada,
 * informando el error sin propagar la excepción hacia la UI.
 */
public class ExportadorArchivo {

    // Directorio de salida para los archivos generados
    private static final String DIRECTORIO_SALIDA = "reportes/";
    private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter FORMATO_LEGIBLE =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ─── Exportación a TXT ────────────────────────────────────────────────────

    /**
     * Exporta una matriz de datos a un archivo .txt con formato tabular.
     *
     * @param encabezados  String[]    — arreglo de nombres de columnas
     * @param datos        String[][]  — matriz de datos a exportar
     * @param nombreBase   Nombre base del archivo (sin extensión ni fecha)
     * @return             Ruta del archivo generado, o null si hubo error
     */
    public String exportarTXT(String[] encabezados, String[][] datos, String nombreBase) {
        crearDirectorioSiNoExiste();

        String timestamp = LocalDateTime.now().format(FORMATO_FECHA);
        String nombreArchivo = DIRECTORIO_SALIDA + nombreBase + "_" + timestamp + ".txt";

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(nombreArchivo), StandardCharsets.UTF_8))) {

            // Encabezado del reporte
            writer.write("========================================");
            writer.newLine();
            writer.write("  SIGAE-Muni — Sistema de Gestión Energética");
            writer.newLine();
            writer.write("  Reporte: " + nombreBase.toUpperCase());
            writer.newLine();
            writer.write("  Generado: " + LocalDateTime.now().format(FORMATO_LEGIBLE));
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            // Calcular ancho de columnas
            int[] anchos = calcularAnchos(encabezados, datos);

            // Escribir encabezados
            StringBuilder lineaEncabezado = new StringBuilder();
            for (int i = 0; i < encabezados.length; i++) {
                lineaEncabezado.append(padDerecha(encabezados[i], anchos[i])).append("  ");
            }
            writer.write(lineaEncabezado.toString().trim());
            writer.newLine();

            // Línea separadora
            StringBuilder separador = new StringBuilder();
            for (int ancho : anchos) {
                separador.append("-".repeat(ancho)).append("  ");
            }
            writer.write(separador.toString().trim());
            writer.newLine();

            // Escribir filas de datos
            for (String[] fila : datos) {
                StringBuilder lineaDatos = new StringBuilder();
                for (int i = 0; i < fila.length && i < anchos.length; i++) {
                    lineaDatos.append(padDerecha(fila[i], anchos[i])).append("  ");
                }
                writer.write(lineaDatos.toString().trim());
                writer.newLine();
            }

            writer.newLine();
            writer.write("Total de registros: " + datos.length);
            writer.newLine();
            writer.write("========================================");
            writer.newLine();

            System.out.println("✓ Archivo TXT generado: " + nombreArchivo);
            return nombreArchivo;

        } catch (IOException e) {
            System.err.println("⚠ Error al exportar TXT: " + e.getMessage());
            return null;
        }
    }

    // ─── Exportación a CSV ────────────────────────────────────────────────────

    /**
     * Exporta una matriz de datos a un archivo .csv compatible con Excel.
     *
     * @param encabezados  String[]    — arreglo de nombres de columnas
     * @param datos        String[][]  — matriz de datos a exportar
     * @param nombreBase   Nombre base del archivo
     * @return             Ruta del archivo generado, o null si hubo error
     */
    public String exportarCSV(String[] encabezados, String[][] datos, String nombreBase) {
        crearDirectorioSiNoExiste();

        String timestamp = LocalDateTime.now().format(FORMATO_FECHA);
        String nombreArchivo = DIRECTORIO_SALIDA + nombreBase + "_" + timestamp + ".csv";

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(nombreArchivo), StandardCharsets.UTF_8))) {

            // BOM para compatibilidad con Excel en Windows
            writer.write('\uFEFF');

            // Escribir encabezados
            writer.write(String.join(";", encabezados));
            writer.newLine();

            // Escribir filas — escapar punto y coma en los valores
            for (String[] fila : datos) {
                String[] filaEscapada = new String[fila.length];
                for (int i = 0; i < fila.length; i++) {
                    filaEscapada[i] = escaparCSV(fila[i]);
                }
                writer.write(String.join(";", filaEscapada));
                writer.newLine();
            }

            System.out.println("✓ Archivo CSV generado: " + nombreArchivo);
            return nombreArchivo;

        } catch (IOException e) {
            System.err.println("⚠ Error al exportar CSV: " + e.getMessage());
            return null;
        }
    }

    // ─── Exportación de resumen estadístico ──────────────────────────────────

    /**
     * Exporta el resumen estadístico (String[]) a un archivo .txt.
     * Demuestra escritura de arreglo unidimensional a archivo.
     *
     * @param resumen  String[] con las líneas del resumen
     * @return         Ruta del archivo generado
     */
    public String exportarResumen(String[] resumen) {
        crearDirectorioSiNoExiste();

        String timestamp    = LocalDateTime.now().format(FORMATO_FECHA);
        String nombreArchivo = DIRECTORIO_SALIDA + "resumen_" + timestamp + ".txt";

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(nombreArchivo), StandardCharsets.UTF_8))) {

            writer.println("========================================");
            writer.println("  SIGAE-Muni — Resumen Estadístico");
            writer.println("  Generado: " + LocalDateTime.now().format(FORMATO_LEGIBLE));
            writer.println("========================================");
            writer.println();

            // Iterar sobre el arreglo unidimensional
            for (String linea : resumen) {
                writer.println("  • " + linea);
            }

            writer.println();
            writer.println("========================================");

            System.out.println("✓ Resumen exportado: " + nombreArchivo);
            return nombreArchivo;

        } catch (IOException e) {
            System.err.println("⚠ Error al exportar resumen: " + e.getMessage());
            return null;
        }
    }

    // ─── Lectura de archivos ──────────────────────────────────────────────────

    /**
     * Lee un archivo de texto y retorna su contenido como ArrayList<String>.
     * Cada línea del archivo es un elemento de la lista.
     *
     * Demuestra recuperación de información desde archivo a estructura dinámica.
     *
     * @param rutaArchivo Ruta completa del archivo a leer
     * @return            ArrayList<String> con las líneas del archivo,
     *                    o lista vacía si hubo error
     */
    public ArrayList<String> leerArchivo(String rutaArchivo) {
        ArrayList<String> lineas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(rutaArchivo), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
            System.out.println("✓ Archivo leído: " + rutaArchivo
                + " (" + lineas.size() + " líneas)");

        } catch (FileNotFoundException e) {
            System.err.println("⚠ Archivo no encontrado: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("⚠ Error al leer archivo: " + e.getMessage());
        }

        return lineas;
    }

    /**
     * Lista todos los reportes generados en el directorio de salida.
     *
     * @return ArrayList<String> con los nombres de archivos disponibles
     */
    public ArrayList<String> listarReportesGenerados() {
        ArrayList<String> archivos = new ArrayList<>();
        File directorio = new File(DIRECTORIO_SALIDA);

        if (!directorio.exists() || !directorio.isDirectory()) {
            return archivos;
        }

        // Usar arreglo nativo para listar archivos del directorio
        File[] listaArchivos = directorio.listFiles(
            (dir, nombre) -> nombre.endsWith(".txt") || nombre.endsWith(".csv")
        );

        if (listaArchivos != null) {
            // Convertir arreglo a ArrayList
            for (File archivo : listaArchivos) {
                archivos.add(archivo.getName());
            }
        }

        return archivos;
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    private void crearDirectorioSiNoExiste() {
        File dir = new File(DIRECTORIO_SALIDA);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Calcula el ancho máximo de cada columna considerando
     * encabezados y datos — usa arreglo de enteros de tamaño fijo.
     */
    private int[] calcularAnchos(String[] encabezados, String[][] datos) {
        int[] anchos = new int[encabezados.length];

        // Inicializar con ancho de encabezados
        for (int i = 0; i < encabezados.length; i++) {
            anchos[i] = encabezados[i].length();
        }

        // Ajustar con ancho de datos
        for (String[] fila : datos) {
            for (int i = 0; i < fila.length && i < anchos.length; i++) {
                if (fila[i] != null && fila[i].length() > anchos[i]) {
                    anchos[i] = fila[i].length();
                }
            }
        }

        return anchos;
    }

    private String padDerecha(String texto, int ancho) {
        if (texto == null) texto = "";
        if (texto.length() >= ancho) return texto;
        return texto + " ".repeat(ancho - texto.length());
    }

    private String escaparCSV(String valor) {
        if (valor == null) return "";
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
