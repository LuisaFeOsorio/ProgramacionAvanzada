package application.services.impl;

import application.dto.comentario.ComentarioDTO;
import application.dto.comentario.CrearComentarioDTO;
import application.dto.comentario.RespuestaComentarioDTO;
import application.dto.paginacion.PaginacionDTO;
import application.mappers.ComentarioMapper;
import application.model.Alojamiento;
import application.model.Comentario;
import application.model.Reserva;
import application.model.Usuario;
import application.repositories.AlojamientoRepository;
import application.repositories.ComentarioRepository;
import application.repositories.ReservaRepository;
import application.repositories.UsuarioRepository;
import application.services.comentario.ComentarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ComentarioMapper comentarioMapper;

    // ✅ CREAR COMENTARIO
    @Override
    public ComentarioDTO crearComentario(String usuarioId, String reservaId, CrearComentarioDTO dto) {
        try {
            // Validar usuario
            Usuario usuario = usuarioRepository.findById(Long.valueOf((usuarioId)))
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Validar reserva
            Reserva reserva = reservaRepository.findById(Long.valueOf(reservaId))
                    .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrado"));

            // Validar que el usuario es el que hizo la reserva
            if (!reserva.getUsuario().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Solo el usuario que hizo la reserva puede comentar");
            }

            // Validar que la reserva está completada
            if (reserva.getEstado() != application.model.enums.EstadoReserva.COMPLETADA) {
                throw new IllegalArgumentException("Solo se puede comentar sobre reservas completadas");
            }

            // Validar que no existe ya un comentario para esta reserva
            if (comentarioRepository.existsByReservaId(reserva.getId())) {
                throw new IllegalArgumentException("Ya existe un comentario para esta reserva");
            }

            // Validar datos del comentario
            validarComentario(dto);

            // Crear comentario
            Comentario comentario = new Comentario();
            comentario.setContenido(dto.contenido());
            comentario.setCalificacion(dto.calificacion());
            comentario.setUsuario(usuario);
            comentario.setReserva(reserva);
            comentario.setAlojamiento(reserva.getAlojamiento());
            comentario.setFechaCreacion(LocalDateTime.now());
            comentario.setActivo(true);

            Comentario comentarioGuardado = comentarioRepository.save(comentario);

            // ✅ ACTUALIZAR CALIFICACIÓN PROMEDIO DEL ALOJAMIENTO
            actualizarCalificacionAlojamiento(reserva.getAlojamiento().getId());

            log.info("Comentario creado exitosamente para reserva {} por usuario {}", reservaId, usuarioId);
            return comentarioMapper.toDTO(comentarioGuardado);

        } catch (IllegalArgumentException e) {
            log.error("Error validando comentario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creando comentario: {}", e.getMessage());
            throw new RuntimeException("Error al crear comentario: " + e.getMessage());
        }
    }

    // ✅ RESPONDER COMENTARIO (ANFITRIÓN)
    @Override
    public RespuestaComentarioDTO responderComentario(String comentarioId, String anfitrionId, RespuestaComentarioDTO respuestaDTO) {
        try {
            // Validar comentario
            Comentario comentario = comentarioRepository.findById(Long.valueOf(comentarioId))
                    .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));

            // Validar anfitrión
            Usuario anfitrion = usuarioRepository.findById(Long.valueOf((anfitrionId)))
                    .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

            // Validar que el anfitrión es dueño del alojamiento
            if (!comentario.getAlojamiento().getAnfitrion().getId().equals(anfitrion.getId())) {
                throw new IllegalArgumentException("Solo el anfitrión puede responder a comentarios de su alojamiento");
            }

            // Validar que el comentario no tiene ya una respuesta
            if (comentario.getRespuesta() != null && !comentario.getRespuesta().trim().isEmpty()) {
                throw new IllegalArgumentException("Este comentario ya tiene una respuesta");
            }

            // Validar respuesta
            if (respuestaDTO.respuesta() == null || respuestaDTO.respuesta().trim().isEmpty()) {
                throw new IllegalArgumentException("La respuesta no puede estar vacía");
            }

            if (respuestaDTO.respuesta().length() > 500) {
                throw new IllegalArgumentException("La respuesta no puede exceder 500 caracteres");
            }

            // Agregar respuesta
            comentario.setRespuesta(respuestaDTO.respuesta());
            comentario.setFechaRespuesta(LocalDateTime.now());

            Comentario comentarioActualizado = comentarioRepository.save(comentario);
            log.info("Respuesta agregada al comentario {} por anfitrión {}", comentarioId, anfitrionId);

            return new RespuestaComentarioDTO(comentarioActualizado.getRespuesta(), comentarioActualizado.getFechaRespuesta());

        } catch (IllegalArgumentException e) {
            log.error("Error respondiendo comentario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error respondiendo al comentario {}: {}", comentarioId, e.getMessage());
            throw new RuntimeException("Error al responder comentario: " + e.getMessage());
        }
    }

    // ✅ LISTAR COMENTARIOS DE UN ALOJAMIENTO
    @Override
    @Transactional(readOnly = true)
    public PaginacionDTO<ComentarioDTO> listarComentariosAlojamiento(String alojamientoId, int pagina, int size) {
        try {
            // Validar alojamiento
            if (!alojamientoRepository.existsById(Long.valueOf(alojamientoId))) {
                throw new IllegalArgumentException("Alojamiento no encontrado");
            }

            Pageable pageable = PageRequest.of(
                    pagina,
                    size,
                    Sort.by("checkIn").descending()
            );

            Page<Comentario> page = comentarioRepository.findByAlojamientoIdAndActivoTrue(
                    Long.valueOf(alojamientoId), pageable);

            List<ComentarioDTO> contenido = page.getContent()
                    .stream()
                    .map(comentarioMapper::toDTO)
                    .collect(Collectors.toList());

            return new PaginacionDTO<>(
                    contenido,
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );

        } catch (IllegalArgumentException e) {
            log.error("Error listando comentarios: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error listando comentarios del alojamiento {}: {}", alojamientoId, e.getMessage());
            throw new RuntimeException("Error al listar comentarios: " + e.getMessage());
        }
    }

    // ✅ OBTENER PROMEDIO DE CALIFICACIÓN
    @Override
    @Transactional(readOnly = true)
    public double obtenerPromedioCalificacion(String alojamientoId) {
        try {
            // Validar alojamiento
            if (!alojamientoRepository.existsById(Long.valueOf(alojamientoId))) {
                throw new IllegalArgumentException("Alojamiento no encontrado");
            }

            Double promedio = comentarioRepository.calcularPromedioCalificacionByAlojamientoId(Long.valueOf(alojamientoId));
            return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0; // Redondear a 1 decimal

        } catch (IllegalArgumentException e) {
            log.error("Error obteniendo promedio de calificación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo promedio de calificación para alojamiento {}: {}", alojamientoId, e.getMessage());
            throw new RuntimeException("Error al obtener promedio de calificación: " + e.getMessage());
        }
    }

    // 🔧 MÉTODOS PRIVADOS AUXILIARES

    private void validarComentario(CrearComentarioDTO dto) {
        if (dto.contenido() == null || dto.contenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del comentario es requerido");
        }

        if (dto.contenido().length() < 10) {
            throw new IllegalArgumentException("El comentario debe tener al menos 10 caracteres");
        }

        if (dto.contenido().length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede exceder 1000 caracteres");
        }

        if (dto.calificacion() == null) {
            throw new IllegalArgumentException("La calificación es requerida");
        }

        if (dto.calificacion() < 1 || dto.calificacion() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
    }

    private void actualizarCalificacionAlojamiento(Long alojamientoId) {
        try {
            Double nuevoPromedio = comentarioRepository.calcularPromedioCalificacionByAlojamientoId(alojamientoId);
            Integer totalComentarios = comentarioRepository.countByAlojamientoIdAndActivoTrue(alojamientoId);

            if (nuevoPromedio != null) {
                Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                        .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

                alojamiento.setCalificacionPromedio(Math.round(nuevoPromedio * 10.0) / 10.0); // Redondear a 1 decimal
                alojamiento.setTotalCalificaciones(totalComentarios);

                alojamientoRepository.save(alojamiento);
                log.info("Calificación actualizada para alojamiento {}: {}", alojamientoId, nuevoPromedio);
            }

        } catch (Exception e) {
            log.error("Error actualizando calificación del alojamiento {}: {}", alojamientoId, e.getMessage());
            // No relanzamos la excepción para no afectar la creación del comentario
        }
    }

}