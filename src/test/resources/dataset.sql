-- ========================================
-- 🔹 DATASET DE PRUEBA - PROYECTO ALOJAMIENTOS
-- ========================================

SET FOREIGN_KEY_CHECKS = 0;

-- Limpieza previa
TRUNCATE TABLE alojamiento_imagenes;
TRUNCATE TABLE alojamiento_servicios;
TRUNCATE TABLE reserva_servicios;
TRUNCATE TABLE comentarios;
TRUNCATE TABLE reservas;
TRUNCATE TABLE notificaciones;
TRUNCATE TABLE alojamientos;
TRUNCATE TABLE usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- ======================
-- 👤 USUARIOS (3 ejemplos)
-- ======================
INSERT INTO usuarios (id, nombre, email, contrasenia, telefono, rol, fecha_nacimiento, foto_perfil, activo, descripcion_personal, documento_identidad, archivo_documentos, documentos_verificados)
VALUES
    (1, 'Carlos Pérez', 'carlos@example.com', '1234', '3001112233', 'USUARIO', '1985-06-15', NULL, TRUE, 'Amante de los viajes y anfitrión desde 2020', 'CC123456', 'docs/carlos.pdf', TRUE),
    (2, 'Luisa Gómez', 'luisa@example.com', 'abcd', '3105557788', 'USUARIO', '1992-04-22', NULL, TRUE, NULL, NULL, NULL, FALSE),
    (3, 'Mariana Torres', 'mariana@example.com', 'xyz789', '3204449966', 'USUARIO', '1990-09-10', NULL, TRUE, 'Ofrece alojamientos rurales en el Quindío', 'CC987654', 'docs/mariana.pdf', TRUE);

-- ======================
-- 🏡 ALOJAMIENTOS (3 ejemplos)
-- ======================
INSERT INTO alojamientos (id, nombre, descripcion, tipo, ciudad, pais, direccion, precio_por_noche, capacidad_maxima, numero_habitaciones, numero_banos, calificacion_promedio, total_calificaciones, activo, fecha_creacion, anfitrion_id)
VALUES
    (1, 'Casa Campestre El Roble', 'Hermosa casa con vista a las montañas del Quindío.', 'CASA', 'Salento', 'Colombia', 'Km 2 vía Cocora', 250000, 6, 3, 2, 4.5, 10, TRUE, NOW(), 1),
    (2, 'Apartamento Moderno Centro', 'Apartamento moderno con todas las comodidades.', 'CASA', 'Armenia', 'Colombia', 'Cra 14 #10-22', 180000, 4, 2, 2, 4.2, 8, TRUE, NOW(), 3),
    (3, 'Cabaña Rústica La Montaña', 'Cabaña rústica ideal para desconectarse y disfrutar la naturaleza.', 'CASA', 'Filandia', 'Colombia', 'Vereda La Paz', 200000, 5, 2, 1, 4.8, 6, TRUE, NOW(), 1);

-- Servicios e imágenes
INSERT INTO alojamiento_servicios (alojamiento_id, servicio) VALUES
                                                                 (1, 'WiFi'), (1, 'Parqueadero'), (1, 'Piscina'),
                                                                 (2, 'WiFi'), (2, 'Aire acondicionado'), (2, 'TV por cable'),
                                                                 (3, 'Chimenea'), (3, 'Senderismo'), (3, 'Mascotas permitidas');

INSERT INTO alojamiento_imagenes (alojamiento_id, url_imagen) VALUES
                                                                  (1, 'img/casa_roble_1.jpg'), (1, 'img/casa_roble_2.jpg'),
                                                                  (2, 'img/apto_moderno_1.jpg'), (2, 'img/apto_moderno_2.jpg'),
                                                                  (3, 'img/cabana_montana_1.jpg'), (3, 'img/cabana_montana_2.jpg');

-- ======================
-- 🗓️ RESERVAS (3 ejemplos)
-- ======================
INSERT INTO reservas (id, check_in, check_out, numero_huespedes, estado, usuario_id, alojamiento_id)
VALUES
    (1, '2025-10-10', '2025-10-15', 2, 'CONFIRMADA', 2, 1),
    (2, '2025-11-01', '2025-11-05', 4, 'PENDIENTE', 2, 2),
    (3, '2025-12-20', '2025-12-27', 3, 'COMPLETADA', 2, 3);

INSERT INTO reserva_servicios (reserva_id, servicio) VALUES
                                                         (1, 'WIFI'), (1, 'DESAYUNO'),
                                                         (2, 'COCINA'), (3, 'WIFI');

-- ======================
-- 💬 COMENTARIOS (3 ejemplos)
-- ======================
INSERT INTO comentarios (id, calificacion, contenido, respuesta, fecha_creacion, fecha_respuesta, activo, usuario_id, alojamiento_id, reserva_id)
VALUES
    (1, 5, 'La casa estaba impecable y el anfitrión fue muy amable. ¡Volvería sin dudarlo!', 'Gracias por tu visita, ¡siempre bienvenida!', NOW(), NOW(), TRUE, 2, 1, 1),
    (2, 4, 'Muy buena ubicación, aunque el WiFi a veces fallaba.', NULL, NOW(), NULL, TRUE, 2, 2, 2),
    (3, 5, 'La cabaña es perfecta para desconectarse. Hermosa vista y tranquilidad.', 'Nos alegra mucho que disfrutaras tu estancia.', NOW(), NOW(), TRUE, 2, 3, 3);

-- ======================
-- 🔔 NOTIFICACIONES (3 ejemplos)
-- ======================
INSERT INTO notificaciones (id, titulo, mensaje, tipo, leida, fecha_creacion, usuario_id)
VALUES
    (1, 'Reserva confirmada', 'Tu reserva para Casa Campestre El Roble ha sido confirmada.', 'RECORDATORIO', FALSE, NOW(), 2),
    (2, 'Nuevo comentario recibido', 'Un huésped ha dejado un nuevo comentario en tu alojamiento.', 'RECORDATORIO', FALSE, NOW(), 1),
    (3, 'Recordatorio de check-in', 'Tu check-in para la Cabaña Rústica es mañana.', 'RECORDATORIO', TRUE, NOW(), 2);
