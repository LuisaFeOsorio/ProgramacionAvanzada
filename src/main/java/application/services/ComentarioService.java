package application.services;

import application.dto.comentario.ComentarioDTO;
import application.dto.comentario.CrearComentarioDTO;
import application.dto.paginacion.PaginacionDTO;
import application.dto.comentario.RespuestaComentarioDTO;

public interface ComentarioService {
    /** Crear comentario asociado a una reserva completada (validar que la reserva fue completada). */
    ComentarioDTO crearComentario(String usuarioId, String reservaId, CrearComentarioDTO dto);

    /** Responder un comentario (solo anfitrión propietario del alojamiento). */
    RespuestaComentarioDTO responderComentario(String comentarioId, String anfitrionId, RespuestaComentarioDTO respuesta);

    /** Obtener lista de comentarios de un alojamiento (ordenado por fecha, paginado). */
    PaginacionDTO<ComentarioDTO> listarComentariosAlojamiento(String alojamientoId, int pagina, int size);

    /** Obtener promedio de calificaciones de un alojamiento. */
    double obtenerPromedioCalificacion(String alojamientoId);
}
