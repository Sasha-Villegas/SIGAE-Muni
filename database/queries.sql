-- ============================================
-- QUERIES.SQL
-- Consultas de demostración para el TP2
-- Ejecutar después de init.sql y seed.sql
-- ============================================

USE sigae_muni;

-- ============================================
-- 1. RANKING SEMANAL DE EDIFICIOS INEFICIENTES
--    Muestra los 3 edificios con mayor desvío positivo
--    en los últimos 7 días.
-- ============================================
SELECT 
    e.nombre,
    ROUND(AVG(l.consumo_kwh), 2) AS consumo_promedio,
    ROUND(
        (SELECT AVG(l2.consumo_kwh)
         FROM lecturas l2
         WHERE l2.medidor_id = l.medidor_id
           AND l2.timestamp BETWEEN '2026-05-01' AND '2026-05-07'
        ), 2
    ) AS linea_base,
    ROUND(
        (AVG(l.consumo_kwh) - 
         (SELECT AVG(l2.consumo_kwh)
          FROM lecturas l2
          WHERE l2.medidor_id = l.medidor_id
            AND l2.timestamp BETWEEN '2026-05-01' AND '2026-05-07')
        ) / 
        (SELECT AVG(l2.consumo_kwh)
         FROM lecturas l2
         WHERE l2.medidor_id = l.medidor_id
           AND l2.timestamp BETWEEN '2026-05-01' AND '2026-05-07'
        ) * 100, 2
    ) AS desvio_porcentaje
FROM lecturas l
JOIN medidores m ON l.medidor_id = m.id
JOIN edificios e ON m.edificio_id = e.id
WHERE l.timestamp >= '2026-05-09'
  AND l.tipo_origen = 'AUTOMATICA'
GROUP BY e.id, e.nombre, l.medidor_id
ORDER BY desvio_porcentaje DESC
LIMIT 3;


-- ============================================
-- 2. INSERCIÓN DE UNA LECTURA MANUAL
--    Simula el registro manual de un operador.
-- ============================================
INSERT INTO lecturas (timestamp, consumo_kwh, tipo_origen, medidor_id)
VALUES (NOW(), 155.0, 'MANUAL', 1);

-- Verificar que se insertó correctamente
SELECT * FROM lecturas WHERE tipo_origen = 'MANUAL';


-- ============================================
-- 3. ELIMINACIÓN DE ALERTAS DESCARTADAS
--    Borra lógicamente alertas que ya no son relevantes.
--    (Antes de ejecutar, asegurate de tener al menos una
--     alerta con estado DESCARTADA. Podés actualizar una 
--     existente con:
--     UPDATE alertas SET estado = 'DESCARTADA' WHERE id = 1;)
-- ============================================
DELETE FROM alertas WHERE estado = 'DESCARTADA';

-- Verificar que la alerta fue eliminada
SELECT * FROM alertas WHERE estado = 'DESCARTADA';


-- ============================================
-- 4. ALERTAS PENDIENTES CON DETALLE DEL EDIFICIO
--    Muestra las alertas que requieren atención,
--    enlazando con la lectura y el edificio.
-- ============================================
SELECT 
    a.id AS alerta_id,
    a.fecha_hora,
    a.tipo,
    a.valor_consumo,
    a.umbral_utilizado,
    a.estado,
    e.nombre AS edificio,
    l.consumo_kwh AS lectura_original,
    l.timestamp AS lectura_fecha
FROM alertas a
JOIN lecturas l ON a.lectura_id = l.id
JOIN medidores m ON l.medidor_id = m.id
JOIN edificios e ON m.edificio_id = e.id
WHERE a.estado = 'PENDIENTE';


-- ============================================
-- 5. CONSULTA DE INTEGRIDAD REFERENCIAL (OPCIONAL)
--    Intento de borrar un edificio con lecturas.
-- ============================================
DELETE FROM edificios WHERE id = 1;
-- Esto debe fallar con un error de foreign key constraint.
-- Si querés capturarlo, ejecutá la línea anterior y
-- tomá pantalla del mensaje de error.