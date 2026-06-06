package sigae.muni.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * MainApp — punto de entrada de la interfaz gráfica JavaFX.
 * Cumple el mismo rol que ConsolaApp pero con ventana en lugar de consola.
 *
 * NO modifica ninguna clase de modelo, persistencia, servicio ni excepciones.
 * Usa exactamente los mismos DAOs y Services que ConsolaApp.
 *
 * Para ejecutar: correr este main en lugar del de ConsolaApp.
 * Para compilar necesitás JavaFX SDK en el classpath/modulepath.
 */
public class MainApp extends Application {

    // Colores del sistema SIGAE (verde teal como primario)
    static final String COLOR_SIDEBAR_BG    = "#FFFFFF";
    static final String COLOR_SIDEBAR_BORDER= "#E5E5E5";
    static final String COLOR_ACTIVE_BG     = "#E1F5EE";
    static final String COLOR_ACTIVE_TEXT   = "#0F6E56";
    static final String COLOR_NAV_TEXT      = "#555555";
    static final String COLOR_LOGO_BG       = "#1D9E75";
    static final String COLOR_PAGE_BG       = "#F5F5F3";
    static final String COLOR_TOPBAR_BG     = "#FFFFFF";
    static final String COLOR_TOPBAR_BORDER = "#E5E5E5";

    private BorderPane rootLayout;
    private VBox sidebar;

    // Botones de navegación (para manejar estado activo)
    private Button btnDashboard;
    private Button btnEdificios;
    private Button btnAlertas;
    private Button btnLecturas;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SIGAE-Muni — Sistema de Monitoreo Energético");

        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: " + COLOR_PAGE_BG + ";");

        sidebar = construirSidebar();
        rootLayout.setLeft(sidebar);

        // Pantalla inicial: Dashboard
        mostrarDashboard();

        Scene scene = new Scene(rootLayout, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    // ─── Sidebar ──────────────────────────────────────────────────────────────

    private VBox construirSidebar() {
        VBox sb = new VBox();
        sb.setPrefWidth(220);
        sb.setMinWidth(220);
        sb.setStyle(
                "-fx-background-color: " + COLOR_SIDEBAR_BG + ";" +
                        "-fx-border-color: " + COLOR_SIDEBAR_BORDER + ";" +
                        "-fx-border-width: 0 1 0 0;"
        );

        // Logo
        sb.getChildren().add(construirLogo());

        // Separador visual
        sb.getChildren().add(separador());

        // Sección Principal
        sb.getChildren().add(labelSeccion("PRINCIPAL"));
        btnDashboard = navButton("⚡  Dashboard",  true);
        btnEdificios = navButton("🏢  Edificios",   false);
        btnLecturas  = navButton("📊  Lecturas",    false);
        btnDashboard.setOnAction(e -> { setActivo(btnDashboard); mostrarDashboard(); });
        btnEdificios.setOnAction(e -> { setActivo(btnEdificios); mostrarEdificios(); });
        btnLecturas .setOnAction(e -> { setActivo(btnLecturas);  mostrarLecturas();  });
        sb.getChildren().addAll(btnDashboard, btnEdificios, btnLecturas);

        // Sección Gestión
        sb.getChildren().add(labelSeccion("GESTIÓN"));
        btnAlertas = navButton("🔔  Alertas",      false);
        btnAlertas.setOnAction(e -> { setActivo(btnAlertas); mostrarAlertas(); });
        sb.getChildren().add(btnAlertas);

        // Spacer para empujar el footer al fondo
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sb.getChildren().add(spacer);

        // Footer
        sb.getChildren().add(separador());
        sb.getChildren().add(footerUsuario());

        return sb;
    }

    private HBox construirLogo() {
        HBox logo = new HBox(10);
        logo.setPadding(new Insets(18, 16, 16, 16));
        logo.setAlignment(Pos.CENTER_LEFT);

        // Ícono cuadrado verde
        Label icono = new Label("⚡");
        icono.setStyle(
                "-fx-background-color: " + COLOR_LOGO_BG + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-min-width: 36px;" +
                        "-fx-min-height: 36px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 8;"
        );

        VBox textos = new VBox(2);
        Label nombre = new Label("SIGAE-Muni");
        nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        Label sub = new Label("Sistema de energía");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
        textos.getChildren().addAll(nombre, sub);

        logo.getChildren().addAll(icono, textos);
        return logo;
    }

    private Button navButton(String texto, boolean activo) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(9, 14, 9, 14));
        btn.setCursor(javafx.scene.Cursor.HAND);
        aplicarEstiloNav(btn, activo);
        return btn;
    }

    private void aplicarEstiloNav(Button btn, boolean activo) {
        if (activo) {
            btn.setStyle(
                    "-fx-background-color: " + COLOR_ACTIVE_BG + ";" +
                            "-fx-text-fill: " + COLOR_ACTIVE_TEXT + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: transparent;" +
                            "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: " + COLOR_NAV_TEXT + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: transparent;" +
                            "-fx-cursor: hand;"
            );
            // Hover effect
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: #F0F0EE;" +
                            "-fx-text-fill: #111111;" +
                            "-fx-font-size: 13px;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: transparent;" +
                            "-fx-cursor: hand;"
            ));
            btn.setOnMouseExited(e -> aplicarEstiloNav(btn, false));
        }
    }

    private void setActivo(Button seleccionado) {
        for (Button b : new Button[]{btnDashboard, btnEdificios, btnLecturas, btnAlertas}) {
            if (b != null) aplicarEstiloNav(b, b == seleccionado);
        }
    }

    private Label labelSeccion(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle(
                "-fx-font-size: 10px;" +
                        "-fx-text-fill: #AAAAAA;" +
                        "-fx-font-weight: bold;"
        );
        lbl.setPadding(new Insets(14, 16, 4, 16));
        return lbl;
    }

    private Region separador() {
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: " + COLOR_SIDEBAR_BORDER + ";");
        return sep;
    }

    private HBox footerUsuario() {
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(12, 14, 12, 14));
        footer.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label("AM");
        avatar.setStyle(
                "-fx-background-color: #E1F5EE;" +
                        "-fx-text-fill: #0F6E56;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-min-width: 30px;" +
                        "-fx-min-height: 30px;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 15;"
        );
        Label nombre = new Label("Admin Municipal");
        nombre.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        footer.getChildren().addAll(avatar, nombre);
        return footer;
    }

    // ─── Navegación entre pantallas ───────────────────────────────────────────

    private void mostrarDashboard() {
        DashboardController ctrl = new DashboardController();
        ctrl.setNavegacion(
                () -> { setActivo(btnEdificios);          mostrarEdificios(); },
                () -> { setActivo(btnLecturas);            mostrarLecturasManual(); },
                () -> { setActivo(btnLecturas);            mostrarLecturasSimulacion(); },
                () -> { setActivo(btnAlertas);             mostrarAlertas(); },
                () -> { setActivo(btnEdificios);           mostrarEdificios(); }
        );
        rootLayout.setCenter(ctrl.getView());
    }

    private void mostrarEdificios() {
        EdificiosController ctrl = new EdificiosController();
        rootLayout.setCenter(ctrl.getView());
    }

    private void mostrarAlertas() {
        AlertasController ctrl = new AlertasController();
        rootLayout.setCenter(ctrl.getView());
    }

    private void mostrarLecturas() {
        mostrarLecturasManual();
    }

    private void mostrarLecturasManual() {
        LecturasController ctrl = new LecturasController();
        ctrl.setSeccionInicial(LecturasController.Seccion.MANUAL);
        rootLayout.setCenter(ctrl.getView());
    }

    private void mostrarLecturasSimulacion() {
        LecturasController ctrl = new LecturasController();
        ctrl.setSeccionInicial(LecturasController.Seccion.SIMULACION);
        rootLayout.setCenter(ctrl.getView());
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        launch(args);
    }
}
