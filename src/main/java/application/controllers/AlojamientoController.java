package application.controllers;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.alojamiento.FiltroBusquedaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.dto.ResponseDTO;
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

    // ✅ CREAR ALOJAMIENTO (SOLO ANFITRIONES)
    @PostMapping
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> crearAlojamiento(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CrearAlojamientoDTO dto) {

        AlojamientoDTO alojamientoCreado = alojamientoService.crearAlojamiento(String.valueOf(usuario.getId()), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Alojamiento creado exitosamente", alojamientoCreado));
    }

    // ✅ OBTENER ALOJAMIENTO POR ID (PÚBLICO)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> obtenerAlojamiento(@PathVariable String id) {
        AlojamientoDTO alojamiento = alojamientoService.obtenerAlojamiento(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento obtenido", alojamiento));
    }

    // ✅ EDITAR ALOJAMIENTO (SOLO ANFITRIÓN PROPIETARIO)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> editarAlojamiento(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id,
            @Valid @RequestBody EditarAlojamientoDTO dto) {

        AlojamientoDTO alojamientoActualizado = alojamientoService.editarAlojamiento(id, dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento actualizado exitosamente", alojamientoActualizado));
    }

    // ✅ ELIMINAR ALOJAMIENTO (SOLO ANFITRIÓN PROPIETARIO)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<String>> eliminarAlojamiento(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id) {

        alojamientoService.eliminarAlojamiento(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento eliminado exitosamente", null));
    }

    // ✅ LISTAR ALOJAMIENTOS DEL ANFITRIÓN AUTENTICADO
    @GetMapping("/mis-alojamientos")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarMisAlojamientos(
            @AuthenticationPrincipal Usuario usuario) {

        List<AlojamientoDTO> alojamientos = alojamientoService.listarAlojamientosAnfitrion(String.valueOf(usuario.getId()));
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamientos obtenidos", alojamientos));
    }

    // ✅ BUSCAR ALOJAMIENTOS CON FILTROS COMPLETOS (PÚBLICO)
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
            @RequestParam(defaultValue = "10") int tamanio) {

        FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(
                ciudad, tipo, precioMin, precioMax, capacidadMin, servicios, query, pagina, tamanio
        );

        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Búsqueda completada", resultado));
    }

    // ✅ BÚSQUEDA RÁPIDA POR CIUDAD Y CAPACIDAD (PÚBLICO)
    @GetMapping("/buscar-rapida")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> busquedaRapida(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) Integer capacidadMin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        FiltroBusquedaDTO filtro = FiltroBusquedaDTO.crear(ciudad, capacidadMin);
        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);

        return ResponseEntity.ok(new ResponseDTO<>(false, "Búsqueda rápida completada", resultado));
    }

    // ✅ VERIFICAR SI PUEDE ELIMINARSE (SOLO ANFITRIÓN)
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

    // ✅ MARCAR IMAGEN PRINCIPAL (SOLO ANFITRIÓN)
    @PutMapping("/{id}/imagen-principal")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> marcarImagenPrincipal(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String id,
            @RequestParam String imagenId) {

        AlojamientoDTO alojamientoActualizado = alojamientoService.marcarImagenPrincipal(id, imagenId);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Imagen principal actualizada", alojamientoActualizado));
    }

    // ✅ ENDPOINT PARA OBTENER TODOS (ADMIN ONLY)
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> listarTodosAlojamientos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {

        FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(
                null, null, null, null, null, null, null, pagina, tamanio
        );

        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Todos los alojamientos", resultado));
    }

    // ✅ OBTENER TIPOS DE ALOJAMIENTO DISPONIBLES (PÚBLICO)
    @GetMapping("/tipos")
    public ResponseEntity<ResponseDTO<TipoAlojamiento[]>> obtenerTiposAlojamiento() {
        return ResponseEntity.ok(new ResponseDTO<>(
                false, "Tipos de alojamiento", TipoAlojamiento.values()
        ));
    }
}