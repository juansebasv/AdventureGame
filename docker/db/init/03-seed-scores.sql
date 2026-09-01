-- ---------------------------------------------------------------------------
-- Datos de ejemplo para la tabla SCORES, para que GET /app/adventure/scores
-- devuelva algo desde el primer arranque.
--
-- Nota: el servicio guarda los nombres SIEMPRE en minusculas
-- (ScoreServiceImp.saveScoreByUser -> name.toLowerCase()), y al leerlos
-- capitaliza la primera letra. Por eso aqui van en minuscula.
-- ---------------------------------------------------------------------------

INSERT INTO scores (name, s_hour, s_minute, s_second, s_timestamp, cellphone) VALUES
('sebastian', 0, 12, 45, '2026-08-30 14:03:00', '3001234567'),
('laura',     0,  9, 30, '2026-08-31 09:15:00', '3009876543'),
('andres',    1,  2,  5, '2026-08-31 18:40:00', '3005556677');
