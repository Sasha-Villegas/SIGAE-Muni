
-- Datos de prueba para SIGAE-Muni

USE sigae_muni;

-- ============================================
-- 1. EDIFICIOS (4 edificios municipales)
-- ============================================
INSERT INTO edificios (nombre, direccion, latitud, longitud, ocupacion_actual) VALUES
('Centro Cívico', 'Av. San Martín 450', -36.8282, -73.0513, 'ALTA'),
('Palacio Municipal', 'Rivadavia 100', -36.8275, -73.0505, 'MEDIA'),
('Hospital Municipal', 'Belgrano 750', -36.8260, -73.0480, 'ALTA'),
('Polideportivo', 'Av. del Libertador 1200', -36.8300, -73.0530, 'BAJA');

-- Verificar
SELECT * FROM edificios;

-- ============================================
-- 2. MEDIDORES (uno por edificio)
-- ============================================
INSERT INTO medidores (numero_serie, tipo, ubicacion, edificio_id) VALUES
('M-CC-001', 'ELECTRICO', 'Tablero Principal', 1),
('M-PM-002', 'ELECTRICO', 'Subsuelo', 2),
('M-HM-003', 'ELECTRICO', 'Planta Baja', 3),
('M-PD-004', 'ELECTRICO', 'Sala de Máquinas', 4);

-- Verificar
SELECT * FROM medidores;

-- ============================================
-- 3. USUARIOS (contraseña de todos: "Abcd1234")
-- Hash bcrypt generado con:
-- https://www.bcryptcalculator.com/ (12 rondas)
-- Password: Abcd1234
-- ============================================
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES
('Admin Sistema', 'admin@dolores.gob.ar', '$2a$12$LJ3m4ys3Lk0TSwHCpNqrFOKgP7LRXxqMqFkGmDB.3GkP3ZvLOeS1q', 'ADMIN'),
('Juan Operador', 'operador@dolores.gob.ar', '$2a$12$LJ3m4ys3Lk0TSwHCpNqrFOKgP7LRXxqMqFkGmDB.3GkP3ZvLOeS1q', 'OPERADOR'),
('María Jefa Mantenimiento', 'jefa@dolores.gob.ar', '$2a$12$LJ3m4ys3Lk0TSwHCpNqrFOKgP7LRXxqMqFkGmDB.3GkP3ZvLOeS1q', 'JEFE_MANTENIMIENTO'),
('Carlos Secretario', 'secretario@dolores.gob.ar', '$2a$12$LJ3m4ys3Lk0TSwHCpNqrFOKgP7LRXxqMqFkGmDB.3GkP3ZvLOeS1q', 'SECRETARIO'),
('Ana Auditora', 'auditor@dolores.gob.ar', '$2a$12$LJ3m4ys3Lk0TSwHCpNqrFOKgP7LRXxqMqFkGmDB.3GkP3ZvLOeS1q', 'AUDITOR');

-- Verificar
SELECT id, nombre, email, rol FROM usuarios;

-- ============================================
-- 4. FACTORES EXTERNOS (clima simulado para 7 días)
-- ============================================
INSERT INTO factores_externos (timestamp, temperatura_celsius, humedad_porcentaje, fuente) VALUES
('2026-05-01 10:00:00', 15.2, 68, 'OpenWeatherMap'),
('2026-05-02 10:00:00', 16.8, 62, 'OpenWeatherMap'),
('2026-05-03 10:00:00', 18.1, 55, 'OpenWeatherMap'),
('2026-05-04 10:00:00', 20.5, 50, 'OpenWeatherMap'),
('2026-05-05 10:00:00', 22.0, 45, 'OpenWeatherMap'),
('2026-05-06 10:00:00', 19.3, 58, 'OpenWeatherMap'),
('2026-05-07 10:00:00', 17.6, 70, 'OpenWeatherMap'),
('2026-05-08 10:00:00', 21.4, 42, 'OpenWeatherMap'),
('2026-05-09 10:00:00', 23.1, 38, 'OpenWeatherMap'),
('2026-05-10 10:00:00', 14.9, 75, 'OpenWeatherMap'),
('2026-05-11 10:00:00', 16.0, 65, 'OpenWeatherMap'),
('2026-05-12 10:00:00', 18.7, 60, 'OpenWeatherMap');

-- Verificar
SELECT * FROM factores_externos;

-- ============================================
-- 5. LECTURAS AUTOMÁTICAS (Centro Cívico, medidor 1)
-- 16 días de datos para que el motor de reglas
-- tenga suficiente histórico (mínimo 15 días)
-- Simula lecturas horarias (una por día a las 10:00)
-- Los últimos 3 días tienen consumo elevado
-- ============================================
INSERT INTO lecturas (timestamp, consumo_kwh, tipo_origen, medidor_id, factor_externo_id) VALUES
-- Días 1 al 13: consumo normal (promedio ~145 kWh)
('2026-05-01 10:00:00', 142.0, 'AUTOMATICA', 1, 1),
('2026-05-02 10:00:00', 145.5, 'AUTOMATICA', 1, 2),
('2026-05-03 10:00:00', 148.2, 'AUTOMATICA', 1, 3),
('2026-05-04 10:00:00', 140.8, 'AUTOMATICA', 1, 4),
('2026-05-05 10:00:00', 143.1, 'AUTOMATICA', 1, 5),
('2026-05-06 10:00:00', 147.0, 'AUTOMATICA', 1, 6),
('2026-05-07 10:00:00', 144.3, 'AUTOMATICA', 1, 7),
('2026-05-08 10:00:00', 141.5, 'AUTOMATICA', 1, 8),
('2026-05-09 10:00:00', 146.8, 'AUTOMATICA', 1, 9),
('2026-05-10 10:00:00', 139.9, 'AUTOMATICA', 1, 10),
('2026-05-11 10:00:00', 145.0, 'AUTOMATICA', 1, 11),
('2026-05-12 10:00:00', 143.7, 'AUTOMATICA', 1, 12),
-- Días 14 al 16: consumo anómalo (sobreconsumo)
('2026-05-13 10:00:00', 195.2, 'AUTOMATICA', 1, 1),
('2026-05-14 10:00:00', 210.5, 'AUTOMATICA', 1, 2),
('2026-05-15 10:00:00', 188.3, 'AUTOMATICA', 1, 3);

-- Verificar
SELECT * FROM lecturas;

-- ============================================
-- 6. UMBRAL DE CONFIGURACIÓN (Centro Cívico)
-- ============================================
INSERT INTO umbrales_configuracion (porcentaje_desvio, dias_historicos, ventana_temperatura, activo, edificio_id) VALUES
(30.0, 15, 2.0, TRUE, 1);

-- Verificar
SELECT * FROM umbrales_configuracion;

-- ============================================
-- 7. ALERTA DE EJEMPLO (generada manualmente)
-- Simula alerta para la lectura del día 14
-- ============================================
INSERT INTO alertas (fecha_hora, tipo, valor_consumo, umbral_utilizado, estado, lectura_id) VALUES
('2026-05-14 10:05:00', 'ANOMALIA_CONSUMO', 210.5, 188.5, 'PENDIENTE', 14);

-- Verificar
SELECT * FROM alertas;

-- ============================================
-- 8. CONSULTAS DE DEMOSTRACIÓN (para capturas)
-- ============================================

-- 8.1 Todos los edificios con sus medidores
SELECT e.nombre AS edificio, m.numero_serie, m.ubicacion
FROM edificios e
JOIN medidores m ON e.id = m.edificio_id;

-- 8.2 Ranking semanal de ineficiencia (últimos 7 días)
SELECT e.nombre,
       ROUND(AVG(l.consumo_kwh), 2) AS consumo_promedio,
       ROUND((SELECT AVG(l2.consumo_kwh)
        FROM lecturas l2
        WHERE l2.medidor_id = l.medidor_id
          AND l2.timestamp BETWEEN '2026-05-01' AND '2026-05-07'), 2) AS linea_base,
       ROUND((AVG(l.consumo_kwh) - (SELECT AVG(l2.consumo_kwh)
        FROM lecturas l2
        WHERE l2.medidor_id = l.medidor_id
          AND l2.timestamp BETWEEN '2026-05-01' AND '2026-05-07'))
        / (SELECT AVG(l2.consumo_kwh)
           FROM lecturas l2
           WHERE l2.medidor_id = l.medidor_id
             AND l2.timestamp BETWEEN '2026-05-01' AND '2026-05-07') * 100, 2) AS desvio_porcentaje
FROM lecturas l
JOIN medidores m ON l.medidor_id = m.id
JOIN edificios e ON m.edificio_id = e.id
WHERE l.timestamp >= '2026-05-09'
  AND l.tipo_origen = 'AUTOMATICA'
GROUP BY e.id, e.nombre, l.medidor_id
ORDER BY desvio_porcentaje DESC
LIMIT 3;

-- 8.3 Alertas pendientes
SELECT a.id, a.fecha_hora, a.tipo, a.valor_consumo, a.estado, e.nombre AS edificio
FROM alertas a
JOIN lecturas l ON a.lectura_id = l.id
JOIN medidores m ON l.medidor_id = m.id
JOIN edificios e ON m.edificio_id = e.id
WHERE a.estado = 'PENDIENTE';

