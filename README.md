# SIGAE-Muni - Sistema Inteligente de Gestión y Ahorro Energético

Repositorio de la base de datos 


## Estructura
- database/init.sql: creación de tablas
- database/seed.sql: datos de prueba
- database/queries.sql: consultas de ejemplo

## Requisitos

- MySQL 8.0 o superior
- Cliente MySQL o herramienta como MySQL Workbench

## Instalación

1. Clonar el repositorio.
2. Ejecutar el script de creación:

   ```bash
   mysql -u root -p < database/init.sql


(Opcional) Cargar datos de prueba:

mysql -u root -p < database/seed.sql


Probar consultas de ejemplo:

mysql -u root -p sigae_muni < database/queries.sql