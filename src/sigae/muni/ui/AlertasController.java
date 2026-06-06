package sigae.muni.ui;

import sigae.muni.modelo.Alerta;
import sigae.muni.persistencia.AlertaDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

/**
 * AlertasController — pantalla de alertas pendientes.
 *
 * Usa: AlertaDAO.
 */
public class AlertasController {

    private final AlertaDAO alertaDAO = new AlertaDAO();
    private VBox listaContainer;

    public VBox getView() {
        VBox view = new VBox();
        view.setStyle("-fx-background-color: #F5F5F3;");

        // Topbar con botón refrescar
        HBox topbar = DashboardController.construirTopbar(
            "Alertas",
            "Alertas pendientes de revisión"
        );
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnRefrescar = new Button("⟳  Refrescar");
        btnRefrescar.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #555555;" +
            "-fx-padding: 5 12 5 12;" +
            "-fx-cursor: hand;"
        );
        btnRefrescar.setOnAction(e -> cargarAlertas());
        topbar.getChildren().addAll(spacer, btnRefrescar);
        view.getChildren().add(topbar);

        VBox contenido = new VBox(16);
        contenido.setPadding(new Insets(20));

        // Resumen de estados
        contenido.getChildren().add(resumenEstados());

        // Tabla de alertas
        Label lblSeccion = new Label("Listado de alertas");
        lblSeccion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        contenido.getChildren().add(lblSeccion);

        listaContainer = new VBox();
        cargarAlertas();
        contenido.getChildren().add(listaContainer);

        view.getChildren().add(contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);

        return view;
    }

    // ─── Resumen con tarjetas de conteo ──────────────────────────────────────

    private HBox resumenEstados() {
        HBox fila = new HBox(12);

        try {
            List<Alerta> alertas = alertaDAO.listarPendientes();
            int total = alertas.size();

            long anomalias = alertas.stream()
                    .filter(a -> "ANOMALIA_CONSUMO".equals(a.getTipo())).count();
            long umbral = alertas.stream()
                    .filter(a -> "UMBRAL_SUPERADO".equals(a.getTipo())).count();

            fila.getChildren().addAll(
                tarjetaResumen("Total pendientes", String.valueOf(total),  "#A32D2D", "#FCEBEB"),
                tarjetaResumen("Anomalía consumo",  String.valueOf(anomalias), "#854F0B", "#FAEEDA"),
                tarjetaResumen("Umbral superado",   String.valueOf(umbral),  "#185FA5", "#E6F1FB")
            );
        } catch (SQLException e) {
            Label err = new Label("⚠ Error al cargar resumen: " + e.getMessage());
            err.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D;");
            fila.getChildren().add(err);
        }

        for (javafx.scene.Node n : fila.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }

        return fila;
    }

    private VBox tarjetaResumen(String titulo, String valor, String colorTexto, String colorFondo) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: #E5E5E5;" +
            "-fx-border-width: 0.5;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;"
        );

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + colorTexto + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    // ─── Tabla de alertas ─────────────────────────────────────────────────────

    private void cargarAlertas() {
        listaContainer.getChildren().clear();
        VBox card = DashboardController.card();

        HBox header = filaAlerta(true, "ID", "Fecha y hora", "Tipo", "Consumo (kWh)", "Umbral usado", "Estado");
        card.getChildren().add(header);
        card.getChildren().add(divisor());

        try {
            List<Alerta> alertas = alertaDAO.listarPendientes();

            if (alertas.isEmpty()) {
                Label vacio = new Label("✓  No hay alertas pendientes.");
                vacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #0F6E56; -fx-padding: 14 16 14 16;");
                card.getChildren().add(vacio);
            } else {
                for (Alerta a : alertas) {
                    HBox fila = filaAlerta(false,
                        String.valueOf(a.getId()),
                        a.getFechaHora().toString().replace("T", " "),
                        a.getTipo(),
                        String.format("%.2f", a.getValorConsumo()),
                        String.format("%.1f%%", a.getUmbralUtilizado()),
                        a.getEstado().name()
                    );
                    card.getChildren().add(fila);
                    card.getChildren().add(divisor());
                }
            }
        } catch (SQLException ex) {
            Label err = new Label("⚠ Error al cargar alertas: " + ex.getMessage());
            err.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D; -fx-padding: 12 16 12 16;");
            card.getChildren().add(err);
        }

        listaContainer.getChildren().add(card);
    }

    private HBox filaAlerta(boolean esHeader, String... celdas) {
        HBox fila = new HBox();
        fila.setPadding(new Insets(10, 16, 10, 16));
        fila.setAlignment(Pos.CENTER_LEFT);

        double[] anchos = {45, 165, 150, 110, 110, 100};
        for (int i = 0; i < celdas.length; i++) {
            Label lbl = new Label(celdas[i]);
            lbl.setStyle(esHeader
                ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #888888;"
                : "-fx-font-size: 12px; -fx-text-fill: #111111;"
            );
            lbl.setPrefWidth(i < anchos.length ? anchos[i] : 100);
            lbl.setWrapText(false);

            // Badge estado
            if (!esHeader && i == celdas.length - 1) {
                lbl.setStyle(
                    "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #A32D2D;" +
                    "-fx-background-color: #FCEBEB; -fx-background-radius: 4; -fx-padding: 2 8 2 8;"
                );
            }
            fila.getChildren().add(lbl);
        }

        if (!esHeader) {
            fila.setOnMouseEntered(e -> fila.setStyle("-fx-background-color: #FFF8F8; -fx-padding: 10 16 10 16;"));
            fila.setOnMouseExited (e -> fila.setStyle("-fx-padding: 10 16 10 16;"));
        }

        return fila;
    }

    private Region divisor() {
        Region r = new Region();
        r.setPrefHeight(0.5);
        r.setStyle("-fx-background-color: #F0F0EE;");
        return r;
    }
}
