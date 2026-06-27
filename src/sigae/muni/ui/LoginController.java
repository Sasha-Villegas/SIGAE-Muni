package sigae.muni.ui;

import sigae.muni.modelo.Usuario;
import sigae.muni.servicio.AuthService;
import sigae.muni.servicio.SesionActual;
import sigae.muni.excepciones.ValidacionException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.sql.SQLException;

/**
 * LoginController — pantalla de inicio de sesión.
 * Es la primera pantalla que ve el usuario al iniciar la aplicación.
 * Valida credenciales contra la BD, inicia la sesión y notifica
 * a MainApp para mostrar la interfaz principal.
 */
public class LoginController {

    private final AuthService authService = new AuthService();

    // Callback que MainApp ejecuta cuando el login es exitoso
    private Runnable onLoginExitoso;

    public void setOnLoginExitoso(Runnable callback) {
        this.onLoginExitoso = callback;
    }

    public VBox getView() {
        // Fondo de pantalla completa
        VBox pantalla = new VBox();
        pantalla.setAlignment(Pos.CENTER);
        pantalla.getStyleClass().add("login-pantalla");

        // Card central del formulario
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40, 48, 40, 48));
        card.setMaxWidth(400);
        card.getStyleClass().add("login-card");

        // ── Logo ─────────────────────────────────────────────────────────────
        HBox logoRow = new HBox(12);
        logoRow.setAlignment(Pos.CENTER);

        Label iconoLogo = new Label("⚡");
        iconoLogo.getStyleClass().add("login-logo-icono");

        VBox logoTextos = new VBox(2);
        Label lblSistema = new Label("SIGAE-Muni");
        lblSistema.getStyleClass().add("login-logo-nombre");
        Label lblSub = new Label("Sistema de Gestión Energética");
        lblSub.getStyleClass().add("login-logo-subtitulo");
        logoTextos.getChildren().addAll(lblSistema, lblSub);

        logoRow.getChildren().addAll(iconoLogo, logoTextos);

        // ── Título ────────────────────────────────────────────────────────────
        Label lblTitulo = new Label("Iniciar sesión");
        lblTitulo.getStyleClass().add("login-titulo");

        Label lblSub2 = new Label("Ingresá tus credenciales para continuar");
        lblSub2.getStyleClass().add("login-subtitulo");
        lblSub2.setTextAlignment(TextAlignment.CENTER);

        // ── Formulario ────────────────────────────────────────────────────────
        VBox formulario = new VBox(12);

        Label lblEmail = new Label("Email");
        lblEmail.getStyleClass().add("login-field-label");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("usuario@municipio.gob.ar");
        txtEmail.getStyleClass().add("login-input");
        txtEmail.setMaxWidth(Double.MAX_VALUE);

        Label lblPass = new Label("Contraseña");
        lblPass.getStyleClass().add("login-field-label");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("••••••••");
        txtPass.getStyleClass().add("login-input");
        txtPass.setMaxWidth(Double.MAX_VALUE);

        // Mensaje de error (oculto por defecto)
        Label lblError = new Label();
        lblError.getStyleClass().add("login-error");
        lblError.setWrapText(true);
        lblError.setMaxWidth(Double.MAX_VALUE);
        lblError.setVisible(false);
        lblError.setManaged(false);

        // Botón ingresar
        Button btnIngresar = new Button("Ingresar");
        btnIngresar.getStyleClass().add("login-btn");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);

        // Acción del botón
        Runnable accionLogin = () -> {
            lblError.setVisible(false);
            lblError.setManaged(false);
            btnIngresar.setDisable(true);
            btnIngresar.setText("Verificando...");

            try {
                Usuario usuario = authService.autenticar(
                    txtEmail.getText(),
                    txtPass.getText()
                );

                // Iniciar sesión con el usuario autenticado
                SesionActual.getInstance().iniciar(usuario);

                // Notificar a MainApp para mostrar la interfaz principal
                if (onLoginExitoso != null) {
                    onLoginExitoso.run();
                }

            } catch (ValidacionException ex) {
                mostrarError(lblError, "⚠ " + ex.getMessage());
            } catch (AuthService.AutenticacionException ex) {
                mostrarError(lblError, "⚠ " + ex.getMessage());
                txtPass.clear();
            } catch (SQLException ex) {
                mostrarError(lblError, "⚠ Error de conexión con la base de datos.");
            } finally {
                btnIngresar.setDisable(false);
                btnIngresar.setText("Ingresar");
            }
        };

        // Permite hacer Enter en el campo de contraseña
        txtPass.setOnAction(e -> accionLogin.run());
        btnIngresar.setOnAction(e -> accionLogin.run());

        formulario.getChildren().addAll(
            lblEmail, txtEmail,
            lblPass, txtPass,
            lblError,
            btnIngresar
        );

        // ── Info de usuarios de prueba ────────────────────────────────────────
        VBox infoUsuarios = new VBox(6);
        infoUsuarios.setAlignment(Pos.CENTER);
        infoUsuarios.getStyleClass().add("login-info-box");

        Label lblInfoTitulo = new Label("Usuarios de prueba");
        lblInfoTitulo.getStyleClass().add("login-info-titulo");

        // Tabla de usuarios de prueba
        String[][] usuarios = {
            {"admin@sigae.muni",      "ADMIN"},
            {"operador@sigae.muni",   "OPERADOR"},
            {"jefe@sigae.muni",       "JEFE"},
            {"secretario@sigae.muni", "SECRETARIO"},
            {"auditor@sigae.muni",    "AUDITOR"},
        };

        infoUsuarios.getChildren().add(lblInfoTitulo);
        for (String[] u : usuarios) {
            HBox fila = new HBox(8);
            fila.setAlignment(Pos.CENTER);
            Label email = new Label(u[0]);
            email.getStyleClass().add("login-info-email");
            Label rol = new Label(u[1]);
            rol.getStyleClass().add("login-info-rol");
            fila.getChildren().addAll(email, rol);
            infoUsuarios.getChildren().add(fila);
        }

        Label lblPassInfo = new Label("Contraseña para todos: sigae2026");
        lblPassInfo.getStyleClass().add("login-info-pass");
        infoUsuarios.getChildren().add(lblPassInfo);

        // ── Armar card ────────────────────────────────────────────────────────
        card.getChildren().addAll(
            logoRow,
            new Separator(),
            lblTitulo,
            lblSub2,
            formulario,
            infoUsuarios
        );

        pantalla.getChildren().add(card);
        return pantalla;
    }

    private void mostrarError(Label lbl, String mensaje) {
        lbl.setText(mensaje);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }
}
