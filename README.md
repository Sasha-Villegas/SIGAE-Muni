# SIGAE-Muni

**Sistema Inteligente de Gestión y Ahorro Energético para Edificios Públicos Municipales**

Prototipo desarrollado para el Seminario de Práctica de la Licenciatura en Informática (Universidad Siglo 21).

## Estado del proyecto

- **TP1 – Fase de Inicio:** Análisis, requerimientos y casos de uso.
- **TP2 – Fase de Elaboración:** Base de datos MySQL, scripts SQL y diagramas UML.
- **TP3 – Fase de Construcción:** Prototipo funcional en Java puro con JDBC.

## Estructura del repositorio
SIGAE-Muni/
├── database/ # Scripts SQL (init.sql, seed.sql, queries.sql)
├── diagrams/ # Diagramas UML del sistema
├── docs/ # Informes escritos (TP1 y TP2)
├── src/ # Código fuente Java del TP3
│ └── sigae/muni/
│ ├── modelo/ # Clases de dominio
│ ├── servicio/ # Lógica de negocio y motor de reglas
│ ├── persistencia/ # Conexión JDBC y DAOs
│ ├── ui/ # Menú de consola
│ └── excepciones/ # Excepciones personalizadas
├── db.properties.example # Plantilla de configuración
├── .gitignore
└── README.md

text

## Requisitos

- Java 17 o superior
- MySQL 8.0 con la base de datos `sigae_muni` creada
- Driver JDBC: `mysql-connector-j-9.1.0.jar`

## Configuración de la base de datos

1. Copiá `db.properties.example` y renombralo como `db.properties`.
2. Editá `db.properties` con tus credenciales de MySQL:
   ```properties
   db.url=jdbc:mysql://localhost:3306/sigae_muni
   db.user=root
   db.password=TU_CONTRASEÑA
El archivo db.properties está excluido del repositorio por seguridad.

Ejecución
Ejecutá la clase principal sigae.muni.ui.ConsolaApp desde tu IDE.

Funcionalidades del menú
Listar edificios

Registrar lectura manual

Simular lectura automática y evaluar alerta

Mostrar alertas pendientes

Cambiar ocupación de un edificio
