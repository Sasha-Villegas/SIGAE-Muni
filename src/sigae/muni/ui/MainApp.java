package sigae.muni.ui;

import sigae.muni.servicio.SesionActual;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * MainApp — punto de entrada de la aplicación.
 *
 * Flujo de arranque:
 *  1. Muestra LoginController (pantalla de login)
 *  2. Al autenticarse correctamente → construye sidebar según permisos del rol
 *  3. Muestra Dashboard como pantalla inicial
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Button btnDashboard;
    private Button btnEdificios;
    private Button btnAlertas;
    private Button btnLecturas;
    private Button btnReportes;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("SIGAE-Muni — Sistema de Monitoreo Energético");
        mostrarLogin();
    }

    // ─── Pantalla de login ────────────────────────────────────────────────────

    private void mostrarLogin() {
        LoginController login = new LoginController();
        login.setOnLoginExitoso(this::iniciarSistema);

        Scene sceneLogin = new Scene(login.getView(), 520, 700);
        sceneLogin.getStylesheets().add(
            getClass().getResource("/sigae/muni/ui/estilos/sigae.css").toExternalForm()
        );

        primaryStage.setScene(sceneLogin);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ─── Inicialización post-login ────────────────────────────────────────────

    private void iniciarSistema() {
        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("root-layout");
        rootLayout.setLeft(construirSidebar());
        mostrarDashboard();

        Scene sceneApp = new Scene(rootLayout, 1000, 650);
        sceneApp.getStylesheets().add(
            getClass().getResource("/sigae/muni/ui/estilos/sigae.css").toExternalForm()
        );

        primaryStage.setScene(sceneApp);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
    }

    // ─── Sidebar con permisos por rol ─────────────────────────────────────────

    private VBox construirSidebar() {
        VBox sb = new VBox();
        sb.setPrefWidth(220);
        sb.setMinWidth(220);
        sb.getStyleClass().add("sidebar");

        sb.getChildren().add(construirLogo());
        sb.getChildren().add(separador());

        // ── Sección Principal ─────────────────────────────────────────────────
        sb.getChildren().add(labelSeccion("PRINCIPAL"));

        // Dashboard: todos los roles
        btnDashboard = navButton("⚡  Dashboard", true);
        btnDashboard.setOnAction(e -> { setActivo(btnDashboard); mostrarDashboard(); });
        sb.getChildren().add(btnDashboard);

        // Edificios: ADMIN, OPERADOR, JEFE
        if (SesionActual.getInstance().puedeVerEdificios()) {
            btnEdificios = navButton("🏢  Edificios", false);
            btnEdificios.setOnAction(e -> { setActivo(btnEdificios); mostrarEdificios(); });
            sb.getChildren().add(btnEdificios);
        }

        // Lecturas: ADMIN, OPERADOR
        if (SesionActual.getInstance().puedeVerLecturas()) {
            btnLecturas = navButton("📊  Lecturas", false);
            btnLecturas.setOnAction(e -> { setActivo(btnLecturas); mostrarLecturas(); });
            sb.getChildren().add(btnLecturas);
        }

        // ── Sección Gestión ───────────────────────────────────────────────────
        sb.getChildren().add(labelSeccion("GESTIÓN"));

        // Alertas: ADMIN, OPERADOR, JEFE, AUDITOR
        if (SesionActual.getInstance().puedeVerAlertas()) {
            btnAlertas = navButton("🔔  Alertas", false);
            btnAlertas.setOnAction(e -> { setActivo(btnAlertas); mostrarAlertas(); });
            sb.getChildren().add(btnAlertas);
        }

        // Reportes: ADMIN, JEFE, SECRETARIO, AUDITOR
        if (SesionActual.getInstance().puedeVerReportes()) {
            btnReportes = navButton("📁  Reportes", false);
            btnReportes.setOnAction(e -> { setActivo(btnReportes); mostrarReportes(); });
            sb.getChildren().add(btnReportes);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sb.getChildren().add(spacer);

        // Footer con nombre y rol del usuario logueado
        sb.getChildren().add(separador());
        sb.getChildren().add(footerUsuario());

        return sb;
    }

    // ─── Logo ─────────────────────────────────────────────────────────────────

    private HBox construirLogo() {
        HBox logo = new HBox(10);
        logo.setPadding(new Insets(18, 16, 16, 16));
        logo.setAlignment(Pos.CENTER_LEFT);

        Label icono = new Label("⚡");
        icono.getStyleClass().add("logo-icono");

        VBox textos = new VBox(2);
        Label nombre = new Label("SIGAE-Muni");
        nombre.getStyleClass().add("logo-nombre");
        Label sub = new Label("Sistema de energía");
        sub.getStyleClass().add("logo-subtitulo");
        textos.getChildren().addAll(nombre, sub);

        logo.getChildren().addAll(icono, textos);
        return logo;
    }

    // ─── Footer con usuario logueado ──────────────────────────────────────────

    private VBox footerUsuario() {
        VBox footer = new VBox(4);
        footer.setPadding(new Insets(12, 14, 12, 14));

        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);

        // Iniciales del usuario
        String nombre = SesionActual.getInstance().getNombre();
        String iniciales = obtenerIniciales(nombre);

        Label avatar = new Label(iniciales);
        avatar.getStyleClass().add("footer-avatar");

        VBox info = new VBox(2);
        Label lblNombre = new Label(nombre);
        lblNombre.getStyleClass().add("footer-nombre");

        // Badge de rol
        Label lblRol = new Label(SesionActual.getInstance().getRol());
        lblRol.getStyleClass().add("badge-verde");

        info.getChildren().addAll(lblNombre, lblRol);
        fila.getChildren().addAll(avatar, info);

        // Botón cerrar sesión
        Button btnCerrar = new Button("Cerrar sesión");
        btnCerrar.getStyleClass().add("btn-outline");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setOnAction(e -> {
            SesionActual.getInstance().cerrar();
            mostrarLogin();
        });

        footer.getChildren().addAll(fila, btnCerrar);
        return footer;
    }

    // ─── Helpers sidebar ──────────────────────────────────────────────────────

    private Button navButton(String texto, boolean activo) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(9, 14, 9, 14));
        aplicarEstiloNav(btn, activo);
        return btn;
    }

    private void aplicarEstiloNav(Button btn, boolean activo) {
        btn.getStyleClass().removeAll("nav-btn", "nav-btn-activo");
        btn.getStyleClass().add(activo ? "nav-btn-activo" : "nav-btn");
    }

    private void setActivo(Button seleccionado) {
        for (Button b : new Button[]{btnDashboard, btnEdificios, btnLecturas, btnAlertas, btnReportes}) {
            if (b != null) aplicarEstiloNav(b, b == seleccionado);
        }
    }

    private Label labelSeccion(String texto) {
        Label lbl = new Label(texto);
        lbl.getStyleClass().add("nav-seccion-label");
        lbl.setPadding(new Insets(14, 16, 4, 16));
        return lbl;
    }

    private Region separador() {
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.getStyleClass().add("separador");
        return sep;
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "?";
        String[] partes = nombre.trim().split(" ");
        if (partes.length >= 2) {
            return String.valueOf(partes[0].charAt(0)).toUpperCase() +
                   String.valueOf(partes[1].charAt(0)).toUpperCase();
        }
        return String.valueOf(nombre.charAt(0)).toUpperCase();
    }

    // ─── Navegación ───────────────────────────────────────────────────────────

    private void mostrarDashboard() {
        DashboardController ctrl = new DashboardController();
        ctrl.setNavegacion(
            () -> { if (btnEdificios != null) { setActivo(btnEdificios); mostrarEdificios(); }},
            () -> { if (btnLecturas  != null) { setActivo(btnLecturas);  mostrarLecturasManual(); }},
            () -> { if (btnLecturas  != null) { setActivo(btnLecturas);  mostrarLecturasSimulacion(); }},
            () -> { if (btnAlertas   != null) { setActivo(btnAlertas);   mostrarAlertas(); }},
            () -> { if (btnEdificios != null) { setActivo(btnEdificios); mostrarEdificios(); }}
        );
        rootLayout.setCenter(ctrl.getView());
    }

    private void mostrarEdificios() {
        rootLayout.setCenter(new EdificiosController().getView());
    }

    private void mostrarAlertas() {
        rootLayout.setCenter(new AlertasController().getView());
    }

    private void mostrarReportes() {
        rootLayout.setCenter(new ReportesController().getView());
    }

    private void mostrarLecturas() { mostrarLecturasManual(); }

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

    public static void main(String[] args) {
        launch(args);
    }
}
