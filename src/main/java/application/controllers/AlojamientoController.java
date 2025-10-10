package application.controllers;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.alojamiento.FiltroBusquedaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.dto.ResponseDTO;
import application.exceptions.alojamiento.*;
import application.model.Usuario;
import application.model.enums.TipoAlojamiento;
import application.services.alojamiento.AlojamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alojamientos")
@RequiredArgsConstructor
public class AlojamientoController {

    private final AlojamientoService alojamientoService;

    @PostMapping
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> crearAlojamiento(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CrearAlojamientoDTO dto) throws CrearAlojamientoException {

        AlojamientoDTO alojamientoCreado = alojamientoService.crearAlojamiento(String.valueOf(usuario.getId()), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Alojamiento creado exitosamente", alojamientoCreado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> obtenerAlojamiento(@PathVariable String id) throws ObtenerAlojamientoException {
        AlojamientoDTO alojamiento = alojamientoService.obtenerAlojamiento(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento obtenido", alojamiento));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> editarAlojamiento(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id,
            @Valid @RequestBody EditarAlojamientoDTO dto) throws EditarAlojamientoException {

        AlojamientoDTO alojamientoActualizado = alojamientoService.editarAlojamiento(id, dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento actualizado exitosamente", alojamientoActualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<String>> eliminarAlojamiento(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id) throws EliminarAlojamientoException {

        alojamientoService.eliminarAlojamiento(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento eliminado exitosamente", null));
    }

    @GetMapping("/mis-alojamientos")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarMisAlojamientos(
            @AuthenticationPrincipal Usuario usuario) throws ListarAlojamientosException {

        List<AlojamientoDTO> alojamientos = alojamientoService.listarAlojamientosAnfitrion(String.valueOf(usuario.getId()));
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamientos obtenidos", alojamientos));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> buscarAlojamientos(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) TipoAlojamiento tipo,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Integer capacidadMin,
            @RequestParam(required = false) List<String> servicios,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) throws BuscarAlojamientoException {

        FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(
                ciudad, tipo, precioMin, precioMax, capacidadMin, servicios, query, pagina, tamanio
        );

        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Búsqueda completada", resultado));
    }

    @GetMapping("/buscar-rapida")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> busquedaRapida(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) Integer capacidadMin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) throws BuscarAlojamientoException {

        FiltroBusquedaDTO filtro = FiltroBusquedaDTO.crear(ciudad, capacidadMin);
        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);

        return ResponseEntity.ok(new ResponseDTO<>(false, "Búsqueda rápida completada", resultado));
    }

    @GetMapping("/{id}/puede-eliminarse")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<Boolean>> puedeEliminarse(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id) {

        boolean puedeEliminarse = alojamientoService.puedeEliminarse(id);
        String mensaje = puedeEliminarse ?
                "Puede eliminarse" : "No puede eliminarse (tiene reservas futuras)";

        return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, puedeEliminarse));
    }

    @PutMapping("/{id}/imagen-principal")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> marcarImagenPrincipal(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id,
            @RequestParam String imagenId) {

        AlojamientoDTO alojamientoActualizado = alojamientoService.marcarImagenPrincipal(id, imagenId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Imagen principal actualizada", alojamientoActualizado));
    }

    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> listarTodosAlojamientos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) throws BuscarAlojamientoException {

        FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(
                null, null, null, null, null, null, null, pagina, tamanio
        );

        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Todos los alojamientos", resultado));
    }

    @GetMapping("/tipos")
    public ResponseEntity<ResponseDTO<TipoAlojamiento[]>> obtenerTiposAlojamiento() {
        return ResponseEntity.ok(new ResponseDTO<>(
                false, "Tipos de alojamiento", TipoAlojamiento.values()
        ));
    }
}