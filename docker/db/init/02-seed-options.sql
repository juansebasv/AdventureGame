-- ---------------------------------------------------------------------------
-- Historia de EJEMPLO para la tabla OPTIONS.
--
-- El contenido original vivía en la base de datos de Heroku, que ya no existe.
-- Este es un árbol de aventura de muestra ("Superviviente en la selva")
-- para que los 3 endpoints funcionen de punta a punta.
--
-- Reglas del motor (ver ControllerAdventure.validateOptions):
--   * El cliente hace POST /app/adventure/{valor} con el id del nodo a mostrar.
--   * Un opt_N_value = 0 significa "esa opción no existe" (el cliente no la pinta).
--   * Nodos terminales (finales): todas las opciones en 0.
--
-- Mapa:
--   1 = inicio
--   6, 9  = finales MALOS
--   12    = final BUENO (ganas)
-- ---------------------------------------------------------------------------

INSERT INTO options (id, description, opt_1_text, opt_2_text, opt_3_text, opt_1_value, opt_2_value, opt_3_value) VALUES
(1,
 'Despiertas tras el accidente de una avioneta en plena selva amazonica. La maleza es densa, huele a tierra mojada y a lo lejos escuchas tambores.',
 'Seguir el sonido de los tambores',
 'Caminar hacia el rio que se oye al oeste',
 'Quedarte junto a los restos de la avioneta y encender una fogata',
 2, 3, 4),

(2,
 'Los tambores te guian hasta un poblado escondido. Varios guerreros te rodean con lanzas y te observan en silencio.',
 'Ofrecerles tu reloj como regalo',
 'Salir corriendo hacia la espesura',
 'Levantar las manos y hablarles con calma',
 5, 6, 7),

(3,
 'Llegas a un rio caudaloso de aguas marrones. Hay una canoa vieja encallada en el barro y, junto a ella, huellas frescas de jaguar.',
 'Tomar la canoa y remar rio abajo',
 'Cruzar el rio a nado',
 'Seguir la orilla a pie',
 8, 9, 2),

(4,
 'El humo de la fogata sube recto entre los arboles. Atrae a una patrulla de rescate... pero tambien a los habitantes del poblado, que llegan primero.',
 'Apagar el fuego y esconderte entre los helechos',
 'Plantarte con una rama encendida en la mano',
 'Rendirte y dejar que te lleven',
 6, 10, 5),

(5,
 'El jefe acepta tu ofrenda con un gesto. Te dan agua fresca, algo de fruta y te senalan un sendero seguro que baja hacia el sur.',
 'Seguir el sendero hacia el sur',
 'Pedir quedarte a descansar en el poblado hasta el amanecer',
 '',
 11, 7, 0),

(6,
 'Corres a ciegas entre la vegetacion y el suelo desaparece bajo tus pies: caes en un foso con estacas. Nadie vendra a buscarte aqui. FIN.',
 '', '', '',
 0, 0, 0),

(7,
 'Tu calma los desconcierta. Una anciana se acerca, te mira a los ojos y dice a los demas que eres "el que cayo del cielo". Se ofrece a guiarte.',
 'Aceptar que la anciana te guie',
 'Desconfiar y marcharte solo hacia el rio',
 '',
 11, 3, 0),

(8,
 'La corriente te arrastra hasta unos rapidos. La canoa se parte, pero logras aferrarte a una roca y salir a una orilla donde hay una torre de vigilancia forestal.',
 'Subir a la torre y usar la radio',
 'Descansar en la orilla hasta el amanecer',
 '',
 12, 10, 0),

(9,
 'A mitad del rio una corriente de fondo te succiona. Luchas, tragas agua y ya no logras encontrar la superficie. FIN.',
 '', '', '',
 0, 0, 0),

(10,
 'Al amanecer, una patrulla de guardabosques te encuentra agotado pero vivo. Te suben a su jeep y te llevan a la base.',
 'Ir con los guardabosques hasta la base',
 '', '',
 12, 0, 0),

(11,
 'El sendero del sur desemboca en una carretera de tala. Un camion maderero frena a tu lado y el conductor te hace senas para que subas.',
 'Subir al camion y viajar hasta el pueblo',
 '', '',
 12, 0, 0),

(12,
 'La radio, el jeep o el camion te llevan por fin a un puesto de la Cruz Roja. Bebes, te curan las heridas y respiras hondo: has sobrevivido a la selva. FIN.',
 '', '', '',
 0, 0, 0);
