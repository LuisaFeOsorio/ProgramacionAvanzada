package application.services.comentario;

import application.dto.comentario.ComentarioDTO;
import application.dto.comentario.CrearComentarioDTO;
import application.dto.comentario.RespuestaComentarioDTO;
import application.dto.paginacion.PaginacionDTO;

public interface ComentarioService {

    ComentarioDTO crearComentario(String usuarioId, String reservaId, CrearComentarioDTO dto);

    RespuestaComentarioDTO responderComentario(String comentarioId, String anfitrionId, RespuestaComentarioDTO respuesta);

    PaginacionDTO<ComentarioDTO> listarComentariosAlojamiento(String alojamientoId, int pagina, int size);

    double obtenerPromedioCalificacion(String alojamientoId);
}
