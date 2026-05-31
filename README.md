# SIGAE-Muni 🏛️⚡

**Sistema Inteligente de Gestión y Ahorro Energético para Edificios Públicos Municipales**

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![JDBC](https://img.shields.io/badge/JDBC-mysql--connector--j--9.1.0-lightgrey?style=flat-square)
![Estado](https://img.shields.io/badge/Estado-En%20desarrollo-yellow?style=flat-square)

Prototipo desarrollado para el Seminario de Práctica de la Licenciatura en Informática — Universidad Siglo 21.

---

## 📋 Descripción

SIGAE-Muni es un sistema de consola desarrollado en Java puro que permite registrar y monitorear el consumo energético de edificios públicos municipales. El sistema evalúa lecturas de consumo, genera alertas automáticas ante valores anómalos y permite gestionar el estado de ocupación de cada edificio.

---

## 🚀 Estado del proyecto

| Fase | Descripción | Estado |
|------|-------------|--------|
| TP1 – Inicio | Análisis, requerimientos y casos de uso | ✅ Completado |
| TP2 – Elaboración | Base de datos MySQL, scripts SQL y diagramas UML | ✅ Completado |
| TP3 – Construcción | Prototipo funcional en Java puro con JDBC | ✅ Completado |

---

## 🛠️ Tecnologías

- **Lenguaje:** Java 17+
- **Base de datos:** MySQL 8.0
- **Conectividad:** JDBC (`mysql-connector-j-9.1.0.jar`)
- **Arquitectura:** Capas (modelo / servicio / persistencia / UI)
- **Paradigma:** Programación orientada a objetos

---

## 📁 Estructura del repositorio

```
SIGAE-Muni/
├── database/               # Scripts SQL (init.sql, seed.sql, queries.sql)
├── diagrams/               # Diagramas UML del sistema
├── docs/                   # Informes escritos (TP1 y TP2)
├── src/                    # Código fuente Java del TP3
│   └── sigae/muni/
│       ├── modelo/         # Clases de dominio
│       ├── servicio/       # Lógica de negocio y motor de reglas
│       ├── persistencia/   # Conexión JDBC y DAOs
│       ├── ui/             # Menú de consola
│       └── excepciones/    # Excepciones personalizadas
├── db.properties   # Plantilla de configuración
├── .gitignore
└── README.md
```

---

## ⚙️ Requisitos previos

- Java 17 o superior
- MySQL 8.0 con la base de datos `sigae_muni` creada
- Driver JDBC: `mysql-connector-j-9.1.0.jar` (incluir en el classpath)

---

## 🗄️ Configuración de la base de datos

1. Ejecutá los scripts en orden desde la carpeta `database/`:
   ```sql
   -- 1. Crear estructura
   source init.sql;
   -- 2. Cargar datos de prueba
   source seed.sql;
   ```

2. Copiá `db.properties` :
   ```
   db.url=jdbc:mysql://localhost:3306/sigae_muni
   db.user=root
   db.password=TU_CONTRASEÑA
   ```

> ⚠️ El archivo `db.properties` está excluido del repositorio por seguridad. **No lo subas a GitHub.**

---

## ▶️ Ejecución

Ejecutá la clase principal desde tu IDE:

```
sigae.muni.ui.ConsolaApp
```

---

## 🖥️ Funcionalidades del menú

```
========== SIGAE-Muni ==========
1. Listar edificios
2. Registrar lectura manual
3. Simular lectura automática y evaluar alerta
4. Mostrar alertas pendientes
5. Cambiar ocupación de un edificio
0. Salir
```

---

## 👩‍💻 Autora

**Sasha Villegas** — Estudiante de Licenciatura en Informática, Universidad Siglo 21  

