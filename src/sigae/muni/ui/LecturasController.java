package sigae.muni.ui;

import sigae.muni.modelo.*;
import sigae.muni.persistencia.*;
import sigae.muni.servicio.*;
import sigae.muni.excepciones.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LecturasController — pantalla de lecturas.
 *
 * Equivale a las opciones:
 *   "2. Registrar nueva lectura manual"
 *   "3. Simular lectura automática y evaluar alerta" de ConsolaApp.
 *
 * Usa: LecturaDAO, MedidorDAO, UmbralDAO, AlertaDAO, MotorReglasService,
 *      ClimaSimulador (todos ya existentes). No modifica nada fuera de ui/.
 */
public class LecturasController {

    public enum Seccion { MANUAL, SIMULACION }

    private Seccion seccionInicial = Seccion.MANUAL;

    public void setSeccionInicial(Seccion s) { this.seccionInicial = s; }

    private final LecturaDAO     lecturaDAO  = new LecturaDAO();
    private final MedidorDAO     medidorDAO  = new MedidorDAO();
    private final UmbralDAO      umbralDAO   = new UmbralDAO();
    private final AlertaDAO      alertaDAO   = new AlertaDAO();
    private final MotorReglasService motor   = new MotorReglasService();

    public VBox getView() {
        VBox view = new VBox();
        view.setStyle("-fx-background-color: #F5F5F3;");

        view.getChildren().add(DashboardController.construirTopbar(
                "Lecturas",
                "Registro manual y simulación automática"
        ));

        VBox contenido = new VBox(16);
        contenido.setPadding(new Insets(20));

        // ── Listado de medidores disponibles ──────────────────────────────────
        contenido.getChildren().add(panelMedidores());

        // ── Dos paneles en columnas ────────────────────────────────────────────
        HBox paneles = new HBox(16);
        paneles.getChildren().addAll(
                panelLecturaManual(),
                panelSimulacionAutomatica()
        );
        HBox.setHgrow(paneles.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(paneles.getChildren().get(1), Priority.ALWAYS);
        contenido.getChildren().add(paneles);

        view.getChildren().add(contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);

        return view;
    }

    // ─── Panel: medidores disponibles ────────────────────────────────────────

    private VBox panelMedidores() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(0));

        Label titulo = new Label("Medidores disponibles");
        titulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        titulo.setPadding(new Insets(14, 16, 10, 16));
        card.getChildren().add(titulo);

        HBox header = filaTabla(true, "ID", "Nro. Serie", "Ubicación", "Edificio ID");
        card.getChildren().add(header);
        card.getChildren().add(divisor());

        try {
            List<Medidor> medidores = medidorDAO.listarTodos();
            if (medidores.isEmpty()) {
                Label vacio = new Label("No hay medidores registrados.");
                vacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-padding: 12 16 12 16;");
                card.getChildren().add(vacio);
            } else {
                for (Medidor m : medidores) {
                    HBox fila = filaTabla(false,
                            String.valueOf(m.getId()),
                            m.getNumeroSerie(),
                            m.getUbicacion(),
                            String.valueOf(m.getEdificioId())
                    );
                    card.getChildren().add(fila);
                    card.getChildren().add(divisor());
                }
            }
        } catch (SQLException ex) {
            Label err = new Label("⚠ Error al cargar medidores: " + ex.getMessage());
            err.setStyle("-fx-font-size: 12px; -fx-text-fill: #A32D2D; -fx-padding: 12 16 12 16;");
            card.getChildren().add(err);
        }

        return card;
    }

    // ─── Panel: lectura manual ────────────────────────────────────────────────

    private VBox panelLecturaManual() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(16));
        card.setSpacing(12);

        Label titulo = new Label("Registrar lectura manual");
        titulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        Label lblId = new Label("ID del medidor");
        lblId.setStyle(estiloLabel());
        TextField txtId = new TextField();
        txtId.setPromptText("Ej: 1");
        txtId.setStyle(estiloInput());

        Label lblConsumo = new Label("Consumo (kWh)");
        lblConsumo.setStyle(estiloLabel());
        TextField txtConsumo = new TextField();
        txtConsumo.setPromptText("Ej: 150.5");
        txtConsumo.setStyle(estiloInput());

        Button btnRegistrar = new Button("Registrar lectura");
        btnRegistrar.setStyle(estiloBtnPrimario());
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setCursor(javafx.scene.Cursor.HAND);

        Label lblResult = new Label();
        lblResult.setWrapText(true);

        btnRegistrar.setOnAction(e -> {
            String idTxt      = txtId.getText().trim();
            String consumoTxt = txtConsumo.getText().trim();

            if (idTxt.isEmpty() || consumoTxt.isEmpty()) {
                setFeedback(lblResult, "⚠ Completá todos los campos.", false);
                return;
            }

            try {
                Long medidorId = Long.parseLong(idTxt);
                double consumo = Double.parseDouble(consumoTxt);

                if (!medidorDAO.existe(medidorId)) {
                    setFeedback(lblResult, "⚠ No existe un medidor con ID " + medidorId + ".", false);
                    return;
                }

                Lectura lectura = new Lectura(null, LocalDateTime.now(), consumo, "MANUAL", medidorId);
                lecturaDAO.insertar(lectura, null);
                setFeedback(lblResult, "✓ Lectura manual registrada correctamente.", true);
                txtId.clear();
                txtConsumo.clear();

            } catch (NumberFormatException ex) {
                setFeedback(lblResult, "⚠ ID y consumo deben ser números válidos.", false);
            } catch (ValidacionException ex) {
                setFeedback(lblResult, "⚠ " + ex.getMessage(), false);
            } catch (SQLException ex) {
                setFeedback(lblResult, "⚠ Error de BD: " + ex.getMessage(), false);
            }
        });

        card.getChildren().addAll(titulo, lblId, txtId, lblConsumo, txtConsumo, btnRegistrar, lblResult);
        return card;
    }

    // ─── Panel: simulación automática ────────────────────────────────────────

    private VBox panelSimulacionAutomatica() {
        VBox card = DashboardController.card();
        card.setPadding(new Insets(16));
        card.setSpacing(12);

        Label titulo = new Label("Simular lectura automática");
        titulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        Label lblId = new Label("ID del medidor");
        lblId.setStyle(estiloLabel());
        TextField txtId = new TextField();
        txtId.setPromptText("Ej: 1");
        txtId.setStyle(estiloInput());

        // Nota: edificioId fijo en 1L igual que ConsolaApp (comentario incluido)
        Label lblNota = new Label("Nota: se evalúa contra el umbral del edificio ID 1 (igual que ConsolaApp).");
        lblNota.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
        lblNota.setWrapText(true);

        Button btnSimular = new Button("Simular y evaluar");
        btnSimular.setStyle(estiloBtnSecundario());
        btnSimular.setMaxWidth(Double.MAX_VALUE);
        btnSimular.setCursor(javafx.scene.Cursor.HAND);

        Label lblResult = new Label();
        lblResult.setWrapText(true);

        // Área de resultado expandida
        TextArea txtDetalle = new TextArea();
        txtDetalle.setEditable(false);
        txtDetalle.setPrefRowCount(5);
        txtDetalle.setStyle(
                "-fx-background-color: #F8F8F6;" +
                        "-fx-border-color: #E5E5E5;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-family: monospace;"
        );
        txtDetalle.setVisible(false);

        btnSimular.setOnAction(e -> {
            String idTxt = txtId.getText().trim();
            if (idTxt.isEmpty()) {
                setFeedback(lblResult, "⚠ Ingresá el ID del medidor.", false);
                return;
            }

            try {
                Long medidorId = Long.parseLong(idTxt);

                // Temperatura simulada (igual que ConsolaApp)
                double temperatura = ClimaSimulador.obtenerTemperatura();

                // Consumo simulado (igual que ConsolaApp: entre 140 y 220 kWh)
                double consumo = 140 + Math.random() * 80;
                Lectura lectura = new Lectura(null, LocalDateTime.now(), consumo, "AUTOMATICA", medidorId);
                lecturaDAO.insertar(lectura, null);

                // Evaluar umbral (edificioId fijo = 1L, igual que ConsolaApp)
                Long edificioId = 1L;
                StringBuilder detalle = new StringBuilder();
                detalle.append(String.format("Temperatura simulada : %.1f°C%n", temperatura));
                detalle.append(String.format("Consumo generado     : %.2f kWh%n", consumo));

                try {
                    UmbralConfiguracion umbral = umbralDAO.buscarActivoPorEdificio(edificioId);
                    List<Lectura> historico = lecturaDAO.listarPorMedidor(medidorId);
                    boolean esAlerta = motor.evaluarLectura(lectura, historico, umbral);

                    if (esAlerta) {
                        Alerta nuevaAlerta = new Alerta(null, LocalDateTime.now(),
                                "ANOMALIA_CONSUMO", consumo, umbral.getPorcentajeDesvio(), lectura.getId());
                        alertaDAO.insertar(nuevaAlerta);
                        detalle.append(String.format("Umbral de desvío     : %.1f%%%n", umbral.getPorcentajeDesvio()));
                        detalle.append("Resultado            : ¡ALERTA! Consumo anómalo detectado.");
                        setFeedback(lblResult, "⚠ Alerta generada — consumo anómalo.", false);
                    } else {
                        detalle.append("Resultado            : Consumo normal. Sin alerta.");
                        setFeedback(lblResult, "✓ Lectura registrada. Consumo normal.", true);
                    }

                } catch (DatosInsuficientesException ex) {
                    detalle.append("Evaluación           : Sin suficientes datos históricos.");
                    setFeedback(lblResult, "⚠ No hay suficientes datos históricos para evaluar.", false);
                }

                txtDetalle.setText(detalle.toString());
                txtDetalle.setVisible(true);
                txtId.clear();

            } catch (NumberFormatException ex) {
                setFeedback(lblResult, "⚠ El ID debe ser un número válido.", false);
            } catch (EntidadNoEncontradaException ex) {
                setFeedback(lblResult, "⚠ No se encontró umbral activo para el edificio 1.", false);
            } catch (SQLException ex) {
                setFeedback(lblResult, "⚠ Error de BD: " + ex.getMessage(), false);
            }
        });

        card.getChildren().addAll(titulo, lblId, txtId, lblNota, btnSimular, lblResult, txtDetalle);
        return card;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private HBox filaTabla(boolean esHeader, String... celdas) {
        HBox fila = new HBox();
        fila.setPadding(new Insets(9, 16, 9, 16));
        fila.setAlignment(Pos.CENTER_LEFT);
        double[] anchos = {50, 160, 200, 90};
        for (int i = 0; i < celdas.length; i++) {
            Label lbl = new Label(celdas[i]);
            lbl.setStyle(esHeader
                    ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #888888;"
                    : "-fx-font-size: 13px; -fx-text-fill: #111111;"
            );
            lbl.setPrefWidth(i < anchos.length ? anchos[i] : 120);
            fila.getChildren().add(lbl);
        }
        if (!esHeader) {
            fila.setOnMouseEntered(e -> fila.setStyle("-fx-background-color: #F8F8F6; -fx-padding: 9 16 9 16;"));
            fila.setOnMouseExited (e -> fila.setStyle("-fx-padding: 9 16 9 16;"));
        }
        return fila;
    }

    private void setFeedback(Label lbl, String mensaje, boolean exito) {
        lbl.setText(mensaje);
        lbl.setStyle(exito
                ? "-fx-font-size: 12px; -fx-text-fill: #0F6E56;"
                : "-fx-font-size: 12px; -fx-text-fill: #A32D2D;"
        );
    }

    private Region divisor() {
        Region r = new Region();
        r.setPrefHeight(0.5);
        r.setStyle("-fx-background-color: #F0F0EE;");
        return r;
    }

    private String estiloLabel() {
        return "-fx-font-size: 12px; -fx-text-fill: #555555; -fx-font-weight: bold;";
    }

    private String estiloInput() {
        return "-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 13px; -fx-padding: 6 10 6 10;";
    }

    private String estiloBtnPrimario() {
        return "-fx-background-color: #1D9E75; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-cursor: hand;";
    }

    private String estiloBtnSecundario() {
        return "-fx-background-color: #185FA5; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-cursor: hand;";
    }
}
