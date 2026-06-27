package sigae.muni.ui;

import sigae.muni.servicio.ExportadorArchivo;
import sigae.muni.servicio.ReporteService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ReportesController — pantalla de reportes y exportación de datos.
 * Permite al usuario:
 *  - Ver el resumen estadístico del sistema
 *  - Ver la tabla de alertas ordenada por consumo descendente
 *  - Exportar a TXT o CSV
 *  - Ver los archivos exportados anteriormente
 * Usa ReporteService (ArrayList + arreglos) y ExportadorArchivo (archivos).
 */
public class ReportesController {

    private final ReporteService     reporteService     = new ReporteService();
    private final ExportadorArchivo  exportador         = new ExportadorArchivo();

    private VBox contenido;
    private Label lblEstado;

    public VBox getView() {
        VBox view = new VBox();
        view.getStyleClass().add("page-bg");

        // Topbar
        HBox topbar = DashboardController.construirTopbar(
            "Reportes", "Exportación de datos a TXT y CSV"
        );
        view.getChildren().add(topbar);

        contenido = new VBox(14);
        contenido.setPadding(new Insets(20));

        lblEstado = new Label();
        lblEstado.setWrapText(true);

        // ── Resumen estadístico ───────────────────────────────────────────────
        contenido.getChildren().add(panelResumen());

        //  Tabla de alertas ordenada
        contenido.getChildren().add(panelTablaAlertas());

        // ── Exportación ───────────────────────────────────────────────────────
        contenido.getChildren().add(panelExportacion());

        // ── Archivos generados ────────────────────────────────────────────────
        contenido.getChildren().add(panelArchivosGenerados());

        contenido.getChildren().add(lblEstado);

        view.getChildren().add(contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return view;
    }

    //  Panel resumen estadístico

    private VBox panelResumen() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(16));
        card.setSpacing(8);

        Label titulo = new Label("Resumen estadístico");
        titulo.getStyleClass().add("texto-seccion");
        card.getChildren().add(titulo);
        card.getChildren().add(divisor());

        try {
            // Obtener arreglo de resumen desde ReporteService
            String[] resumen = reporteService.generarResumenEstadistico();

            // Iterar sobre el arreglo unidimensional para mostrar cada métrica
            HBox grilla = new HBox(12);


            for (String linea : resumen) {
                String[] partes = linea.split(": ", 2);
                if (partes.length == 2) {
                    VBox metrica = new VBox(3);
                    metrica.setPadding(new Insets(10, 14, 10, 14));
                    metrica.setStyle(
                        "-fx-background-color: #F8F8F6;" +
                        "-fx-border-color: #E5E5E5;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
                    );
                    Label lbl = new Label(partes[0]);
                    lbl.getStyleClass().add("texto-caption");
                    Label val = new Label(partes[1]);
                    val.getStyleClass().addAll("texto-titulo");
                    metrica.getChildren().addAll(lbl, val);
                    grilla.getChildren().add(metrica);
                }
            }
            card.getChildren().add(grilla);

        } catch (SQLException e) {
            card.getChildren().add(labelError("Error al cargar resumen: " + e.getMessage()));
        }

        return card;
    }

    // ─── Panel tabla de alertas ordenada ─────────────────────────────────────

    private VBox panelTablaAlertas() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(0));

        HBox header = new HBox();
        header.setPadding(new Insets(14, 16, 10, 16));
        header.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label("Alertas pendientes — ordenadas por consumo (desc)");
        titulo.getStyleClass().add("texto-seccion");
        Label badge = new Label("Algoritmo: burbuja sobre arreglo");
        badge.getStyleClass().add("badge-info");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titulo, spacer, badge);
        card.getChildren().add(header);

        try {
            String[][] matriz = reporteService.generarMatrizAlertas();
            String[] encabezados = ReporteService.ENCABEZADOS_ALERTAS;

            // Fila de encabezados
            card.getChildren().add(filaTabla(encabezados, true));
            card.getChildren().add(divisor());

            if (matriz.length == 0) {
                Label vacio = new Label("✓  No hay alertas pendientes.");
                vacio.getStyleClass().add("mensaje-ok");
                card.getChildren().add(vacio);
            } else {
                for (String[] fila : matriz) {
                    card.getChildren().add(filaTabla(fila, false));
                    card.getChildren().add(divisor());
                }
            }

        } catch (SQLException e) {
            card.getChildren().add(labelError("Error al cargar alertas: " + e.getMessage()));
        }

        return card;
    }

    // ─── Panel exportación ────────────────────────────────────────────────────

    private VBox panelExportacion() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(16));
        card.setSpacing(10);

        Label titulo = new Label("Exportar datos");
        titulo.getStyleClass().add("texto-seccion");
        card.getChildren().add(titulo);
        card.getChildren().add(divisor());

        HBox botones = new HBox(10);
        botones.setPadding(new Insets(8, 0, 0, 0));

        Button btnAlertasTXT = boton("Alertas → TXT", "#1D9E75");
        Button btnAlertasCSV = boton("Alertas → CSV", "#185FA5");
        Button btnEdificiosTXT = boton("Edificios → TXT", "#854F0B");
        Button btnEdificiosCSV = boton("Edificios → CSV", "#5B2D8E");
        Button btnResumen = boton("Resumen estadístico → TXT", "#555555");

        btnAlertasTXT.setOnAction(e -> exportarAlertas("txt"));
        btnAlertasCSV.setOnAction(e -> exportarAlertas("csv"));
        btnEdificiosTXT.setOnAction(e -> exportarEdificios("txt"));
        btnEdificiosCSV.setOnAction(e -> exportarEdificios("csv"));
        btnResumen.setOnAction(e -> exportarResumen());

        botones.getChildren().addAll(
            btnAlertasTXT, btnAlertasCSV, btnEdificiosTXT, btnEdificiosCSV, btnResumen
        );
        card.getChildren().add(botones);
        return card;
    }

    // ─── Panel archivos generados ─────────────────────────────────────────────

    private VBox panelArchivosGenerados() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(16));
        card.setSpacing(8);

        HBox headerFila = new HBox();
        headerFila.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label("Archivos exportados");
        titulo.getStyleClass().add("texto-seccion");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnRefrescar = new Button("⟳  Actualizar");
        btnRefrescar.getStyleClass().add("btn-outline");

        VBox listaContainer = new VBox(4);
        btnRefrescar.setOnAction(e -> cargarListaArchivos(listaContainer));

        headerFila.getChildren().addAll(titulo, sp, btnRefrescar);
        card.getChildren().addAll(headerFila, divisor());
        cargarListaArchivos(listaContainer);
        card.getChildren().add(listaContainer);
        return card;
    }

    private void cargarListaArchivos(VBox container) {
        container.getChildren().clear();
        // Lee lista como ArrayList<String> desde ExportadorArchivo
        ArrayList<String> archivos = exportador.listarReportesGenerados();
        if (archivos.isEmpty()) {
            Label vacio = new Label("No hay archivos exportados todavía.");
            vacio.getStyleClass().add("mensaje-vacio");
            container.getChildren().add(vacio);
        } else {
            for (String nombre : archivos) {
                Label lbl = new Label("📄  " + nombre);
                lbl.getStyleClass().add("texto-muted");
                container.getChildren().add(lbl);
            }
        }
    }

    //  Acciones de exportación

    private void exportarAlertas(String formato) {
        try {
            String[][] matriz = reporteService.generarMatrizAlertas();
            String ruta;
            if ("csv".equals(formato)) {
                ruta = exportador.exportarCSV(
                    ReporteService.ENCABEZADOS_ALERTAS, matriz, "alertas"
                );
            } else {
                ruta = exportador.exportarTXT(
                    ReporteService.ENCABEZADOS_ALERTAS, matriz, "alertas"
                );
            }
            setFeedback(ruta != null
                ? "✓ Exportado correctamente: " + ruta
                : "⚠ Error al exportar.", ruta != null);
        } catch (SQLException e) {
            setFeedback("⚠ Error de BD: " + e.getMessage(), false);
        }
    }

    private void exportarEdificios(String formato) {
        try {
            String[][] matriz = reporteService.generarMatrizEdificios();
            String ruta;
            if ("csv".equals(formato)) {
                ruta = exportador.exportarCSV(
                    ReporteService.ENCABEZADOS_EDIFICIOS, matriz, "edificios"
                );
            } else {
                ruta = exportador.exportarTXT(
                    ReporteService.ENCABEZADOS_EDIFICIOS, matriz, "edificios"
                );
            }
            setFeedback(ruta != null
                ? "✓ Exportado correctamente: " + ruta
                : "⚠ Error al exportar.", ruta != null);
        } catch (SQLException e) {
            setFeedback("⚠ Error de BD: " + e.getMessage(), false);
        }
    }

    private void exportarResumen() {
        try {
            String[] resumen = reporteService.generarResumenEstadistico();
            String ruta = exportador.exportarResumen(resumen);
            setFeedback(ruta != null
                ? "✓ Resumen exportado: " + ruta
                : "⚠ Error al exportar.", ruta != null);
        } catch (SQLException e) {
            setFeedback("⚠ Error de BD: " + e.getMessage(), false);
        }
    }

    // ─── Helpers UI ──────────────────────────────────────────────────────────

    private HBox filaTabla(String[] celdas, boolean esHeader) {
        HBox fila = new HBox();
        fila.setPadding(new Insets(9, 16, 9, 16));
        fila.setAlignment(Pos.CENTER_LEFT);
        double[] anchos = {45, 155, 140, 90, 80, 90};
        for (int i = 0; i < celdas.length; i++) {
            Label lbl = new Label(celdas[i]);
            lbl.getStyleClass().add(esHeader ? "tabla-header-label" : "tabla-cell-sm");
            lbl.setPrefWidth(i < anchos.length ? anchos[i] : 100);
            fila.getChildren().add(lbl);
        }
        if (!esHeader) {
            fila.setOnMouseEntered(e -> fila.getStyleClass().add("tabla-fila-hover"));
            fila.setOnMouseExited (e -> fila.getStyleClass().remove("tabla-fila-hover"));
        }
        return fila;
    }

    private Button boton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + color + ";");
        btn.getStyleClass().add("btn-primario");
        return btn;
    }

    private void setFeedback(String mensaje, boolean exito) {
        lblEstado.setText(mensaje);
        lblEstado.getStyleClass().removeAll("feedback-ok","feedback-error");
        lblEstado.getStyleClass().add(exito ? "feedback-ok" : "feedback-error");
    }

    private Region divisor() {
        Region r = new Region();
        r.setPrefHeight(0.5);
        r.getStyleClass().add("divisor");
        return r;
    }

    private Label labelError(String texto) {
        Label lbl = new Label("⚠ " + texto);
        lbl.getStyleClass().add("mensaje-error");
        return lbl;
    }
}
