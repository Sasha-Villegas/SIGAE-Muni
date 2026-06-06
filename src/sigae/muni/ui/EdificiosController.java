package sigae.muni.ui;

import sigae.muni.modelo.Edificio;
import sigae.muni.servicio.EdificioService;
import sigae.muni.excepciones.EntidadNoEncontradaException;
import sigae.muni.excepciones.ValidacionException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

/**
 * EdificiosController — pantalla de edificios.
 *
 * Equivale a las opciones:
 *   "1. Listar edificios" y 5.
 *
 * Usa: EdificioService.
 */
public class EdificiosController {

    private final EdificioService edificioService = new EdificioService();
    private VBox listaContainer;

    public VBox getView() {
        VBox view = new VBox();
        view.setStyle("-fx-background-color: #F5F5F3;");

        view.getChildren().add(DashboardController.construirTopbar(
            "Edificios",
            "Listado y gestión de ocupación"
        ));

        VBox contenido = new VBox(16);
        contenido.setPadding(new Insets(20));

        // ── Panel superior: formulario cambiar ocupación ──────────────────────
        contenido.getChildren().add(panelCambiarOcupacion());

        // ── Lista de edificios ────────────────────────────────────────────────
        Label lblSeccion = new Label("Edificios registrados");
        lblSeccion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        contenido.getChildren().add(lblSeccion);

        listaContainer = new VBox();
        cargarEdificios();
        contenido.getChildren().add(listaContainer);

        view.getChildren().add(contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);

        return view;
    }

    // ─── Formulario cambio de ocupación ──────────────────────────────────────

    private VBox panelCambiarOcupacion() {
        VBox panel = DashboardController.card();
        panel.setPadding(new Insets(16));
        panel.setSpacing(12);

        Label titulo = new Label("Cambiar ocupación de edificio");
        titulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);

        // Campo ID
        TextField txtId = new TextField();
        txtId.setPromptText("ID del edificio");
        txtId.setPrefWidth(140);
        txtId.setStyle(estiloInput());

        // ComboBox ocupación
        ComboBox<String> comboOcup = new ComboBox<>();
        comboOcup.getItems().addAll("BAJA", "MEDIA", "ALTA");
        comboOcup.setPromptText("Ocupación");
        comboOcup.setPrefWidth(140);
        comboOcup.setStyle(estiloInput());

        // Botón confirmar
        Button btnCambiar = new Button("Actualizar");
        btnCambiar.setStyle(estiloBtnPrimario());
        btnCambiar.setCursor(javafx.scene.Cursor.HAND);

        // Label de feedback
        Label lblFeedback = new Label();
        lblFeedback.setStyle("-fx-font-size: 12px;");

        btnCambiar.setOnAction(e -> {
            String idTexto   = txtId.getText().trim();
            String ocupacion = comboOcup.getValue();

            if (idTexto.isEmpty() || ocupacion == null) {
                lblFeedback.setText("⚠ Completá el ID y seleccioná la ocupación.");
                lblFeedback.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D;");
                return;
            }

            try {
                Long id = Long.parseLong(idTexto);
                edificioService.actualizarOcupacion(id, ocupacion);
                lblFeedback.setText("✓ Ocupación actualizada correctamente.");
                lblFeedback.setStyle("-fx-font-size: 12px; -fx-text-fill: #0F6E56;");
                txtId.clear();
                comboOcup.setValue(null);
                cargarEdificios(); // refrescar lista
            } catch (NumberFormatException ex) {
                lblFeedback.setText("⚠ El ID debe ser un número.");
                lblFeedback.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D;");
            } catch (EntidadNoEncontradaException ex) {
                lblFeedback.setText("⚠ No existe un edificio con ese ID.");
                lblFeedback.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D;");
            } catch (ValidacionException ex) {
                lblFeedback.setText("⚠ " + ex.getMessage());
                lblFeedback.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D;");
            } catch (SQLException ex) {
                lblFeedback.setText("⚠ Error de base de datos: " + ex.getMessage());
                lblFeedback.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D;");
            }
        });

        fila.getChildren().addAll(txtId, comboOcup, btnCambiar);
        panel.getChildren().addAll(titulo, fila, lblFeedback);
        return panel;
    }

    // ─── Lista de edificios ───────────────────────────────────────────────────

    private void cargarEdificios() {
        listaContainer.getChildren().clear();
        VBox card = DashboardController.card();

        // Encabezado
        HBox header = filaEdificio(true, "ID", "Nombre", "Dirección", "Latitud", "Longitud", "Ocupación");
        card.getChildren().add(header);
        card.getChildren().add(divisor());

        try {
            List<Edificio> edificios = edificioService.listarTodos();

            if (edificios.isEmpty()) {
                Label vacio = new Label("No hay edificios registrados.");
                vacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-padding: 14 16 14 16;");
                card.getChildren().add(vacio);
            } else {
                for (Edificio e : edificios) {
                    HBox fila = filaEdificio(false,
                        String.valueOf(e.getId()),
                        e.getNombre(),
                        e.getDireccion(),
                        String.format("%.4f", e.getLatitud()),
                        String.format("%.4f", e.getLongitud()),
                        e.getOcupacionActual()
                    );
                    card.getChildren().add(fila);
                    card.getChildren().add(divisor());
                }
            }
        } catch (SQLException ex) {
            Label err = new Label("⚠ Error al cargar edificios: " + ex.getMessage());
            err.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D; -fx-padding: 12 16 12 16;");
            card.getChildren().add(err);
        }

        listaContainer.getChildren().add(card);
    }

    private HBox filaEdificio(boolean esHeader, String... celdas) {
        HBox fila = new HBox();
        fila.setPadding(new Insets(10, 16, 10, 16));
        fila.setAlignment(Pos.CENTER_LEFT);

        double[] anchos = {50, 180, 200, 90, 90, 90};
        for (int i = 0; i < celdas.length; i++) {
            Label lbl = new Label(celdas[i]);
            lbl.setStyle(esHeader
                ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #888888;"
                : "-fx-font-size: 13px; -fx-text-fill: #111111;"
            );
            lbl.setPrefWidth(i < anchos.length ? anchos[i] : 120);
            lbl.setWrapText(false);

            // Badge de ocupación
            if (!esHeader && i == celdas.length - 1) {
                lbl.setStyle(estiloOcupacion(celdas[i]));
            }

            fila.getChildren().add(lbl);
        }

        if (!esHeader) {
            fila.setOnMouseEntered(e -> fila.setStyle("-fx-background-color: #F8F8F6; -fx-padding: 10 16 10 16;"));
            fila.setOnMouseExited (e -> fila.setStyle("-fx-padding: 10 16 10 16;"));
        }

        return fila;
    }

    private String estiloOcupacion(String ocupacion) {
        return switch (ocupacion) {
            case "ALTA"  -> "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #854F0B; -fx-background-color: #FAEEDA; -fx-background-radius: 4; -fx-padding: 2 8 2 8;";
            case "MEDIA" -> "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #185FA5; -fx-background-color: #E6F1FB; -fx-background-radius: 4; -fx-padding: 2 8 2 8;";
            case "BAJA"  -> "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F6E56; -fx-background-color: #E1F5EE; -fx-background-radius: 4; -fx-padding: 2 8 2 8;";
            default      -> "-fx-font-size: 11px; -fx-text-fill: #555555;";
        };
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Region divisor() {
        Region r = new Region();
        r.setPrefHeight(0.5);
        r.setStyle("-fx-background-color: #F0F0EE;");
        return r;
    }

    private String estiloInput() {
        return "-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 13px; -fx-padding: 6 10 6 10;";
    }

    private String estiloBtnPrimario() {
        return "-fx-background-color: #1D9E75; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 7 16 7 16; -fx-cursor: hand;";
    }
}
