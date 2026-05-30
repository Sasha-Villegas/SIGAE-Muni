package sigae.muni.ui;

import sigae.muni.modelo.*;
import sigae.muni.persistencia.*;
import sigae.muni.servicio.*;
import sigae.muni.excepciones.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsolaApp {
    private static Scanner sc = new Scanner(System.in);
    private static EdificioService edificioService = new EdificioService();
    private static LecturaDAO lecturaDAO = new LecturaDAO();
    private static UmbralDAO umbralDAO = new UmbralDAO();
    private static AlertaDAO alertaDAO = new AlertaDAO();
    private static MotorReglasService motor = new MotorReglasService();

    public static void main(String[] args) {
        System.out.println("=== SIGAE-Muni - Prototipo Java TP3 ===");
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            try {
                procesarOpcion(opcion);
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n--- Menú Principal ---");
        System.out.println("1. Listar edificios");
        System.out.println("2. Registrar nueva lectura manual");
        System.out.println("3. Simular lectura automática y evaluar alerta");
        System.out.println("4. Mostrar alertas pendientes");
        System.out.println("5. Cambiar ocupación de un edificio");
        System.out.println("0. Salir");
    }

    private static void procesarOpcion(int opcion) throws SQLException {
        switch (opcion) {
            case 1: listarEdificios(); break;
            case 2: registrarLecturaManual(); break;
            case 3: simularLecturaAutomatica(); break;
            case 4: mostrarAlertas(); break;
            case 5: cambiarOcupacion(); break;
            case 0: System.out.println("Hasta luego!"); break;
            default: System.out.println("Opción inválida.");
        }
    }

    private static void listarEdificios() throws SQLException {
        List<Edificio> edificios = edificioService.listarTodos();
        System.out.println("\n--- Edificios ---");
        for (Edificio e : edificios) {
            System.out.println(e);
        }
    }

    private static void registrarLecturaManual() throws SQLException {
        MedidorDAO medidorDAO = new MedidorDAO();

        // Mostrar medidores disponibles
        List<Medidor> medidores = medidorDAO.listarTodos();
        System.out.println("\n--- Medidores disponibles ---");
        for (Medidor m : medidores) {
            System.out.printf("ID: %d | Serie: %s | Ubicación: %s\n",
                    m.getId(), m.getNumeroSerie(), m.getUbicacion());
        }

        // Pedir ID y validar
        long medidorId;
        while (true) {
            System.out.print("ID del medidor (0 para cancelar): ");
            medidorId = sc.nextLong();
            sc.nextLine();
            if (medidorId == 0) {
                System.out.println("Operación cancelada.");
                return;
            }
            if (medidorDAO.existe(medidorId)) {
                break;
            }
            System.out.println("Error: No existe un medidor con ese ID. Intente nuevamente.");
        }

        // Pedir consumo
        System.out.print("Consumo (kWh): ");
        double consumo = sc.nextDouble();
        sc.nextLine();

        // Crear y guardar lectura
        Lectura lectura = new Lectura(null, LocalDateTime.now(), consumo, "MANUAL", medidorId);
        lecturaDAO.insertar(lectura, null);
        System.out.println("Lectura manual registrada con éxito.");
    }

    private static void simularLecturaAutomatica() throws SQLException {
        System.out.print("ID del medidor: ");
        Long medidorId = sc.nextLong();
        sc.nextLine();

        // Simular temperatura
        double temperatura = ClimaSimulador.obtenerTemperatura();
        System.out.printf("Temperatura simulada: %.1f°C\n", temperatura);

        // Insertar factor externo (opcional, solo para referencia)
        // ... (podríamos insertar en factores_externos si se desea)

        // Crear lectura automática
        double consumo = 140 + Math.random() * 80; // Entre 140 y 220 kWh
        Lectura lectura = new Lectura(null, LocalDateTime.now(), consumo, "AUTOMATICA", medidorId);
        lecturaDAO.insertar(lectura, null); // Sin factorExterno por simplicidad
        System.out.printf("Lectura automática: %.2f kWh\n", consumo);

        // Obtener umbral activo para el edificio del medidor
        // Nota: Necesitamos el edificioId. Podemos obtenerlo del medidor (deberíamos tener MedidorDAO).
        // Como simplificación, usamos un edificio fijo (1) para la demo.
        Long edificioId = 1L; // ¡Ajustar según tu BD!
        try {
            UmbralConfiguracion umbral = umbralDAO.buscarActivoPorEdificio(edificioId);
            List<Lectura> historico = lecturaDAO.listarPorMedidor(medidorId);
            boolean alerta = motor.evaluarLectura(lectura, historico, umbral);
            if (alerta) {
                System.out.println("¡ALERTA! Consumo anómalo detectado.");
                Alerta nuevaAlerta = new Alerta(null, LocalDateTime.now(), "ANOMALIA_CONSUMO",
                        consumo, umbral.getPorcentajeDesvio(), lectura.getId());
                alertaDAO.insertar(nuevaAlerta);
            } else {
                System.out.println("Consumo normal.");
            }
        } catch (DatosInsuficientesException e) {
            System.out.println("No hay suficientes datos históricos para evaluar.");
        }
    }

    private static void mostrarAlertas() throws SQLException {
        List<Alerta> alertas = alertaDAO.listarPendientes();
        System.out.println("\n--- Alertas Pendientes ---");
        for (Alerta a : alertas) {
            System.out.printf("ID: %d | Fecha: %s | Consumo: %.2f | Estado: %s\n",
                    a.getId(), a.getFechaHora(), a.getValorConsumo(), a.getEstado());
        }
    }

    private static void cambiarOcupacion() throws SQLException {
        System.out.print("ID del edificio: ");
        Long id = sc.nextLong();
        sc.nextLine();
        System.out.print("Nueva ocupación (BAJA/MEDIA/ALTA): ");
        String ocup = sc.nextLine().toUpperCase();
        edificioService.actualizarOcupacion(id, ocup);
        System.out.println("Ocupación actualizada.");
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextInt()) {
            System.out.println("Debe ingresar un número.");
            sc.next();
        }
        int n = sc.nextInt();
        sc.nextLine(); // limpiar buffer
        return n;
    }
}