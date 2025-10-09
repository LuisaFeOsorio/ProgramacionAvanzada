package application.controllers;

import application.dto.comentario.ComentarioDTO;
import application.dto.comentario.CrearComentarioDTO;
import application.dto.ResponseDTO;
import application.dto.comentario.RespuestaComentarioDTO;
import application.dto.paginacion.PaginacionDTO;
import application.model.Usuario;
import application.services.comentario.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    // ✅ CREAR COMENTARIO (SOLO USUARIOS CON RESERVA COMPLETADA)
    @PostMapping("/reserva/{reservaId}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ResponseDTO<ComentarioDTO>> crearComentario(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String reservaId,
            @Valid @RequestBody CrearComentarioDTO dto) {

        ComentarioDTO comentarioCreado = comentarioService.crearComentario(String.valueOf(usuario.getId()), reservaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Comentario creado exitosamente", comentarioCreado));
    }

    // ✅ RESPONDER COMENTARIO (SOLO ANFITRIÓN PROPIETARIO)
    @PostMapping("/{comentarioId}/respuesta")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<RespuestaComentarioDTO>> responderComentario(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String comentarioId,
            @Valid @RequestBody RespuestaComentarioDTO dto) {

        RespuestaComentarioDTO respuesta = comentarioService.responderComentario(comentarioId, String.valueOf(usuario.getId()), dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Respuesta agregada exitosamente", respuesta));
    }

    // ✅ LISTAR COMENTARIOS DE UN ALOJAMIENTO (PÚBLICO)
    @GetMapping("/alojamiento/{alojamientoId}")
    public ResponseEntity<ResponseDTO<PaginacionDTO<ComentarioDTO>>> listarComentariosAlojamiento(
            @PathVariable String alojamientoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        PaginacionDTO<ComentarioDTO> comentarios = comentarioService.listarComentariosAlojamiento(alojamientoId, pagina, tamanio);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Comentarios obtenidos", comentarios));
    }

    // ✅ OBTENER PROMEDIO DE CALIFICACIÓN (PÚBLICO)
    @GetMapping("/alojamiento/{alojamientoId}/calificacion-promedio")
    public ResponseEntity<ResponseDTO<Double>> obtenerCalificacionPromedio(
            @PathVariable String alojamientoId) {

        double promedio = comentarioService.obtenerPromedioCalificacion(alojamientoId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Calificación promedio obtenida", promedio));
    }

    // ✅ OBTENER COMENTARIOS POR USUARIO (SOLO USUARIO PROPIETARIO O ADMIN)
    @GetMapping("/mis-comentarios")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ResponseDTO<PaginacionDTO<ComentarioDTO>>> listarMisComentarios(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        // Nota: Necesitarías agregar este método al servicio
        // PaginacionDTO<ComentarioDTO> comentarios = comentarioService.listarComentariosPorUsuario(usuario.getId(), pagina, tamanio);
        // return ResponseEntity.ok(new ResponseDTO<>(false, "Mis comentarios", comentarios));

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new ResponseDTO<>(true, "Funcionalidad en desarrollo", null));
    }

    // ✅ OBTENER COMENTARIOS SIN RESPONDER (SOLO ANFITRIÓN)
    @GetMapping("/anfitrion/sin-respuesta")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<PaginacionDTO<ComentarioDTO>>> listarComentariosSinRespuesta(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        // Nota: Necesitarías agregar este método al servicio
        // PaginacionDTO<ComentarioDTO> comentarios = comentarioService.listarComentariosSinRespuestaPorAnfitrion(usuario.getId(), pagina, tamanio);
        // return ResponseEntity.ok(new ResponseDTO<>(false, "Comentarios sin respuesta", comentarios));

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new ResponseDTO<>(true, "Funcionalidad en desarrollo", null));
    }

    // ✅ ELIMINAR COMENTARIO (SOLO ADMIN O USUARIO PROPIETARIO)
    @DeleteMapping("/{comentarioId}")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<String>> eliminarComentario(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String comentarioId) {

        // Nota: Necesitarías agregar este método al servicio
        // comentarioService.eliminarComentario(comentarioId, usuario.getId());
        // return ResponseEntity.ok(new ResponseDTO<>(false, "Comentario eliminado", null));

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new ResponseDTO<>(true, "Funcionalidad en desarrollo", null));
    }

    // ✅ OBTENER ESTADÍSTICAS DE COMENTARIOS (SOLO ANFITRIÓN)
    @GetMapping("/anfitrion/estadisticas")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<Object>> obtenerEstadisticasComentarios(
            @AuthenticationPrincipal Usuario usuario) {

        // Nota: Podrías agregar un método para estadísticas
        // Ej: total comentarios, promedio general, comentarios sin respuesta, etc.

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new ResponseDTO<>(true, "Funcionalidad en desarrollo", null));
    }
}