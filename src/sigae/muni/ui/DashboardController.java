package sigae.muni.ui;

import sigae.muni.modelo.Alerta;
import sigae.muni.modelo.Edificio;
import sigae.muni.persistencia.AlertaDAO;
import sigae.muni.servicio.EdificioService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

/**
 * DashboardController — pantalla principal del sistema.
 *
 * Incluye botonera central con las mismas 5 opciones de ConsolaApp
 * más métricas y tablas de resumen.
 *
 * Recibe una referencia al navegador (Runnable) para poder cambiar
 * de pantalla desde los botones del menú visual.
 */
public class DashboardController {

    private final EdificioService edificioService = new EdificioService();
    private final AlertaDAO alertaDAO = new AlertaDAO();

    // Navegación: referencias pasadas desde MainApp
    private Runnable irEdificios;
    private Runnable irLecturaManual;
    private Runnable irSimulacion;
    private Runnable irAlertas;
    private Runnable irCambiarOcupacion;

    public DashboardController() {}

    /** Registra las acciones de navegación desde MainApp */
    public void setNavegacion(
            Runnable irEdificios,
            Runnable irLecturaManual,
            Runnable irSimulacion,
            Runnable irAlertas,
            Runnable irCambiarOcupacion) {
        this.irEdificios        = irEdificios;
        this.irLecturaManual    = irLecturaManual;
        this.irSimulacion       = irSimulacion;
        this.irAlertas          = irAlertas;
        this.irCambiarOcupacion = irCambiarOcupacion;
    }

    public VBox getView() {
        VBox view = new VBox();
        view.setStyle("-fx-background-color: #F5F5F3;");

        view.getChildren().add(construirTopbar("Dashboard", "Menú principal — SIGAE-Muni"));

        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(24));

        // ── Métricas ──────────────────────────────────────────────────────────
        List<Edificio> edificios = null;
        List<Alerta>   alertas   = null;
        try {
            edificios = edificioService.listarTodos();
            alertas   = alertaDAO.listarPendientes();
        } catch (SQLException e) {
            contenido.getChildren().add(labelError("Error al conectar con la base de datos: " + e.getMessage()));
            view.getChildren().add(contenido);
            return view;
        }

        int totalEdificios = edificios.size();
        long edificiosAlta = edificios.stream()
                .filter(e -> "ALTA".equals(e.getOcupacionActual())).count();
        int totalAlertas = alertas.size();

        HBox tarjetas = new HBox(12);
        tarjetas.getChildren().addAll(
                tarjetaMetrica("Edificios registrados", String.valueOf(totalEdificios),
                        "Total en el sistema", "#185FA5", "#E6F1FB"),
                tarjetaMetrica("Ocupación ALTA", String.valueOf(edificiosAlta),
                        "Edificios en alta demanda", "#854F0B", "#FAEEDA"),
                tarjetaMetrica("Alertas pendientes", String.valueOf(totalAlertas),
                        "Requieren revisión",
                        totalAlertas > 0 ? "#A32D2D" : "#0F6E56",
                        totalAlertas > 0 ? "#FCEBEB" : "#E1F5EE")
        );
        for (javafx.scene.Node n : tarjetas.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        contenido.getChildren().add(tarjetas);

        // ── Separador con título ──────────────────────────────────────────────
        Label lblMenu = new Label("Menú Principal");
        lblMenu.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        Label lblSub = new Label("Seleccioná una opción para continuar");
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
        VBox tituloMenu = new VBox(3, lblMenu, lblSub);
        contenido.getChildren().add(tituloMenu);

        // ── Botonera del menú (igual que ConsolaApp) ──────────────────────────
        contenido.getChildren().add(botonera());

        view.getChildren().add(contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return view;
    }

    // ─── Botonera central ─────────────────────────────────────────────────────

    private GridPane botonera() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        // Columnas iguales
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, new ColumnConstraints() {{ setPercentWidth(50); }});

        grid.add(btnMenu("1", "📋  Listar edificios",
                "Ver todos los edificios registrados\ncon su ocupación actual.",
                "#185FA5", "#E6F1FB",
                irEdificios), 0, 0);

        grid.add(btnMenu("2", "✏️  Registrar lectura manual",
                "Ingresá el ID del medidor\ny el consumo en kWh.",
                "#1D9E75", "#E1F5EE",
                irLecturaManual), 1, 0);

        grid.add(btnMenu("3", "⚡  Simular lectura automática",
                "Genera una lectura aleatoria\ny evalúa si hay alerta.",
                "#854F0B", "#FAEEDA",
                irSimulacion), 0, 1);

        grid.add(btnMenu("4", "🔔  Mostrar alertas pendientes",
                "Listado de alertas con estado\nPENDIENTE en el sistema.",
                "#A32D2D", "#FCEBEB",
                irAlertas), 1, 1);

        grid.add(btnMenu("5", "🏢  Cambiar ocupación",
                "Actualizá el nivel de ocupación\nde un edificio (BAJA/MEDIA/ALTA).",
                "#5B2D8E", "#F0E9FB",
                irCambiarOcupacion), 0, 2);

        // Botón salir
        grid.add(btnSalir(), 1, 2);

        return grid;
    }

    private VBox btnMenu(String numero, String titulo, String descripcion,
                         String colorTexto, String colorFondo, Runnable accion) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(estiloCard(colorFondo, "#DDDDDD"));
        card.setMaxWidth(Double.MAX_VALUE);

        HBox encabezado = new HBox(10);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label(numero);
        badge.setStyle(
                "-fx-background-color: " + colorTexto + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-min-width: 26px;" +
                        "-fx-min-height: 26px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 13;"
        );

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + colorTexto + ";"
        );

        encabezado.getChildren().addAll(badge, lblTitulo);

        Label lblDesc = new Label(descripcion);
        lblDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        lblDesc.setWrapText(true);

        card.getChildren().addAll(encabezado, lblDesc);

        // Hover
        card.setOnMouseEntered(e -> card.setStyle(estiloCardHover(colorFondo)));
        card.setOnMouseExited (e -> card.setStyle(estiloCard(colorFondo, "#DDDDDD")));

        if (accion != null) {
            card.setOnMouseClicked(e -> accion.run());
        }

        return card;
    }

    private VBox btnSalir() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(estiloCard("#F5F5F3", "#DDDDDD"));
        card.setMaxWidth(Double.MAX_VALUE);

        HBox encabezado = new HBox(10);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label("0");
        badge.setStyle(
                "-fx-background-color: #888888;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-min-width: 26px;" +
                        "-fx-min-height: 26px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 13;"
        );

        Label lblTitulo = new Label("🚪  Salir");
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555555;");
        encabezado.getChildren().addAll(badge, lblTitulo);

        Label lblDesc = new Label("Cerrar la aplicación SIGAE-Muni.");
        lblDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        card.getChildren().addAll(encabezado, lblDesc);

        card.setOnMouseEntered(e -> card.setStyle(estiloCardHover("#EEEEEE")));
        card.setOnMouseExited (e -> card.setStyle(estiloCard("#F5F5F3", "#DDDDDD")));
        card.setOnMouseClicked(e -> javafx.application.Platform.exit());

        return card;
    }

    private String estiloCard(String bg, String border) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;";
    }

    private String estiloCardHover(String bg) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-border-color: #BBBBBB;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);";
    }

    // ─── Helpers compartidos ──────────────────────────────────────────────────

    static HBox construirTopbar(String titulo, String subtitulo) {
        HBox topbar = new HBox();
        topbar.setPadding(new Insets(14, 20, 14, 20));
        topbar.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #E5E5E5;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        topbar.setAlignment(Pos.CENTER_LEFT);

        VBox textos = new VBox(2);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        Label lblSub = new Label(subtitulo);
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
        textos.getChildren().addAll(lblTitulo, lblSub);

        topbar.getChildren().add(textos);
        return topbar;
    }

    static VBox card() {
        VBox card = new VBox();
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #E5E5E5;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );
        card.setPadding(new Insets(0));
        return card;
    }

    private VBox tarjetaMetrica(String titulo, String valor, String subtitulo,
                                String colorTexto, String colorFondo) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14, 16, 14, 16));
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
        lblValor.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + colorTexto + ";");

        HBox badge = new HBox();
        badge.setStyle(
                "-fx-background-color: " + colorFondo + ";" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 2 8 2 8;"
        );
        Label lblBadge = new Label(subtitulo);
        lblBadge.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorTexto + ";");
        badge.getChildren().add(lblBadge);

        card.getChildren().addAll(lblTitulo, lblValor, badge);
        return card;
    }

    private Label labelError(String texto) {
        Label lbl = new Label("⚠ " + texto);
        lbl.setStyle(
                "-fx-font-size: 13px; -fx-text-fill: #A32D2D;" +
                        "-fx-background-color: #FCEBEB; -fx-background-radius: 8;" +
                        "-fx-padding: 12 16 12 16;"
        );
        lbl.setPadding(new Insets(20));
        return lbl;
    }
}
