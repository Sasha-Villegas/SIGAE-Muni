
CREATE DATABASE IF NOT EXISTS sigae_muni;
USE sigae_muni;

CREATE TABLE edificios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    direccion VARCHAR(200) NOT NULL,
    latitud DECIMAL(10,8),
    longitud DECIMAL(11,8),
    ocupacion_actual ENUM('BAJA','MEDIA','ALTA') DEFAULT 'MEDIA'
);

CREATE TABLE medidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_serie VARCHAR(50) NOT NULL,
    tipo ENUM('ELECTRICO') NOT NULL,
    ubicacion VARCHAR(100),
    edificio_id INT NOT NULL,
    FOREIGN KEY (edificio_id) REFERENCES edificios(id) ON DELETE RESTRICT
);

CREATE TABLE factores_externos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp DATETIME NOT NULL,
    temperatura_celsius DOUBLE,
    humedad_porcentaje DOUBLE,
    fuente VARCHAR(50) DEFAULT 'OpenWeatherMap'
);

CREATE TABLE lecturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp DATETIME NOT NULL,
    consumo_kwh DOUBLE NOT NULL CHECK (consumo_kwh >= 0),
    tipo_origen ENUM('MANUAL','AUTOMATICA') NOT NULL,
    medidor_id INT NOT NULL,
    factor_externo_id BIGINT,
    FOREIGN KEY (medidor_id) REFERENCES medidores(id) ON DELETE CASCADE,
    FOREIGN KEY (factor_externo_id) REFERENCES factores_externos(id) ON DELETE SET NULL,
    INDEX idx_lecturas_medidor_ts (medidor_id, timestamp)
);

CREATE TABLE umbrales_configuracion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    porcentaje_desvio DOUBLE NOT NULL CHECK (porcentaje_desvio BETWEEN 10 AND 100),
    dias_historicos INT NOT NULL CHECK (dias_historicos >= 15),
    ventana_temperatura DOUBLE NOT NULL CHECK (ventana_temperatura > 0),
    activo BOOLEAN DEFAULT TRUE,
    edificio_id INT UNIQUE,
    FOREIGN KEY (edificio_id) REFERENCES edificios(id) ON DELETE CASCADE
);

CREATE TABLE alertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    tipo ENUM('ANOMALIA_CONSUMO','UMBRAL_SUPERADO') NOT NULL,
    valor_consumo DOUBLE NOT NULL,
    umbral_utilizado DOUBLE NOT NULL,
    estado ENUM('PENDIENTE','INVESTIGADA','DESCARTADA') DEFAULT 'PENDIENTE',
    lectura_id BIGINT NOT NULL,
    FOREIGN KEY (lectura_id) REFERENCES lecturas(id) ON DELETE CASCADE,
    INDEX idx_alertas_estado (estado)
);

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    rol ENUM('OPERADOR','JEFE_MANTENIMIENTO','SECRETARIO','AUDITOR','ADMIN') NOT NULL
);