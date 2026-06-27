# SIGAE-Muni 🏛️⚡
**Sistema Inteligente de Gestión y Ahorro Energético para Edificios Públicos Municipales**

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-green?style=flat-square&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.4-blue?style=flat-square&logo=mysql)
![Aiven](https://img.shields.io/badge/BD-Aiven%20Cloud-purple?style=flat-square)
![HikariCP](https://img.shields.io/badge/Pool-HikariCP-red?style=flat-square)
![Estado](https://img.shields.io/badge/Estado-TP4%20Completado-brightgreen?style=flat-square)

Proyecto integrador desarrollado para el Seminario de Práctica de la Licenciatura en Informática — Universidad Empresarial Siglo 21.

---

## 🎥 Video de demostración

<!-- VIDEO_PLACEHOLDER -->
> El video de demostración del sistema será publicado aquí próximamente.

---

## 📋 Descripción

SIGAE-Muni es un sistema de información para la gestión energética de edificios públicos municipales. Permite registrar y monitorear el consumo eléctrico, generar alertas automáticas ante anomalías mediante un motor de reglas configurable, y exportar reportes de eficiencia energética. El sistema cuenta con interfaz gráfica JavaFX, autenticación de usuarios con control de acceso por roles (RBAC) y base de datos MySQL alojada en la nube (Aiven).

---

## 🚀 Estado del proyecto

| Fase | Descripción | Estado |
|------|-------------|--------|
| TP1 – Inicio | Análisis del problema, requerimientos y justificación | ✅ Completado |
| TP2 – Elaboración | Base de datos MySQL, diagramas UML, casos de uso | ✅ Completado |
| TP3 – Construcción | Prototipo Java con JDBC, POO, menú de consola | ✅ Completado |
| TP4 – Integración | Interfaz JavaFX, login, roles, reportes, archivos, nube | ✅ Completado |

---

## 🛠️ Tecnologías

- **Lenguaje:** Java 17
- **Interfaz gráfica:** JavaFX 21
- **Base de datos:** MySQL 8.4 — Aiven Cloud
- **Conectividad:** JDBC (`mysql-connector-j-9.7.0.jar`)
- **Connection pool:** HikariCP 5.0.1
- **Seguridad:** SHA-256 para hash de contraseñas
- **Arquitectura:** Capas (modelo / servicio / persistencia / ui)
- **Patrones de diseño:** Strategy, DAO, Singleton
- **Paradigma:** Programación orientada a objetos

---

## 📁 Estructura del repositorio

```
SIGAE-Muni/
├── database/
│   ├── init.sql                  # Creación de tablas
│   ├── seed_completo_aiven.sql   # Datos de prueba + usuarios SHA-256
│   └── queries.sql               # Consultas de demostración
├── diagrams/                     # Diagramas UML del sistema
├── docs/                         # Informes escritos (TP1, TP2, TP3, TP4)
├── src/
│   └── sigae/muni/
│       ├── modelo/               # Entidades del dominio
│       ├── servicio/             # Lógica de negocio, motor de reglas, auth
│       ├── persistencia/         # JDBC, DAOs, HikariCP
│       ├── ui/
│       │   ├── estilos/          # sigae.css — estilos centralizados
│       │   ├── LoginController   # Pantalla de inicio de sesión
│       │   ├── MainApp           # Punto de entrada JavaFX
│       │   └── ...controllers    # Dashboard, Edificios, Alertas, Lecturas, Reportes
│       └── excepciones/          # Excepciones personalizadas
├── db.properties                 # Configuración de BD (no incluido en repo)
├── .gitignore
└── README.md
```

---

## ⚙️ Requisitos previos

- Java 17 o superior
- JavaFX SDK 21 ([descargar en gluonhq.com](https://gluonhq.com/products/javafx/))
- Driver JDBC: `mysql-connector-j-9.7.0.jar`
- HikariCP: `HikariCP-5.0.1.jar` + `slf4j-api-2.0.9.jar` + `slf4j-simple-2.0.9.jar`

---

## 🗄️ Configuración de la base de datos

La base de datos está alojada en **Aiven Cloud**. Para configurar la conexión, creá el archivo `db.properties` en la raíz del proyecto:

```properties
db.url=jdbc:mysql://<host>:<port>/defaultdb?ssl-mode=REQUIRED&useSSL=true&requireSSL=true&serverTimezone=UTC
db.user=avnadmin
db.password=TU_PASSWORD
```

Para ejecutar en local, usá:

```properties
db.url=jdbc:mysql://localhost:3306/sigae_muni?serverTimezone=UTC
db.user=root
db.password=TU_PASSWORD
```

> ⚠️ El archivo `db.properties` está excluido del repositorio por seguridad. **No lo subas a GitHub.**

---

## ▶️ Ejecución

**Interfaz gráfica (recomendada):**

Configurá las VM options en IntelliJ:
```
--module-path "ruta/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
```
Ejecutá la clase principal:
```
sigae.muni.ui.MainApp
```

**Interfaz de consola (TP3):**
```
sigae.muni.ui.ConsolaApp
```

---

## 🔐 Usuarios de prueba

| Email | Rol | Acceso |
|-------|-----|--------|
| admin@sigae.muni | ADMIN | Todo |
| operador@sigae.muni | OPERADOR | Edificios, Lecturas, Alertas |
| jefe@sigae.muni | JEFE | Edificios, Alertas, Reportes |
| secretario@sigae.muni | SECRETARIO | Reportes |
| auditor@sigae.muni | AUDITOR | Alertas, Reportes |

> Contraseña para todos: `sigae2026`

---

## 🖥️ Funcionalidades principales

- ✅ Login con autenticación SHA-256 y control de acceso por roles (RBAC)
- ✅ Dashboard con métricas de consumo en tiempo real
- ✅ Registro de lecturas manuales y simulación automática
- ✅ Motor de reglas configurable para detección de anomalías (patrón Strategy)
- ✅ Gestión de alertas con ciclo de vida (PENDIENTE → INVESTIGADA → DESCARTADA)
- ✅ Reportes exportables a TXT y CSV con ordenamiento burbuja
- ✅ Interfaz gráfica JavaFX con estilos centralizados en CSS
- ✅ Base de datos MySQL en la nube (Aiven) con HikariCP connection pool

---

## 👩‍💻 Autora

**Sasha Villegas Basso** — Legajo VINF015771
Licenciatura en Informática — Universidad Empresarial Siglo 21
Seminario de Práctica — Profesora Ana Carolina Ferreyra

---


