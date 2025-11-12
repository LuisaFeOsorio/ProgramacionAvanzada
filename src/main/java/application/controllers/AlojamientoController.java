package application.controllers;

import application.dto.ResponseDTO;
import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.alojamiento.FiltroBusquedaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.exceptions.alojamiento.*;
import application.exceptions.usuario.UsuarioNoEncontradoException;
import application.model.Alojamiento;
import application.model.Usuario;
import application.model.enums.TipoAlojamiento;
import application.services.alojamiento.AlojamientoService;
import application.services.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alojamientos")
@RequiredArgsConstructor
public class AlojamientoController {

    private final AlojamientoService alojamientoService;
    private final UsuarioService usuarioService;

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> crearAlojamiento(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CrearAlojamientoDTO dto) throws CrearAlojamientoException, UsuarioNoEncontradoException {

        System.out.println("🏠 === INICIANDO CREACIÓN DE ALOJAMIENTO ===");

        String email = userDetails.getUsername();
        System.out.println("📧 Email del usuario autenticado: " + email);

        Usuario usuario = usuarioService.findByEmail(email);
        System.out.println("🔍 Usuario encontrado: " + (usuario != null ? usuario.getNombre() : "NULL"));

        if (usuario == null) {
            System.out.println("❌ Usuario no encontrado en la base de datos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
        }

        System.out.println("✅ Usuario ID: " + usuario.getId());
        System.out.println("📦 DTO recibido: " + dto.toString());

        AlojamientoDTO alojamientoCreado = alojamientoService.crearAlojamiento(String.valueOf(usuario.getId()), dto);

        System.out.println("🎉 Alojamiento creado exitosamente con ID: " +
                (alojamientoCreado != null ? alojamientoCreado.id() : "NULL"));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, "Alojamiento creado exitosamente", alojamientoCreado));
    }

    @PostMapping("/crear-simple")
    public ResponseEntity<AlojamientoDTO> crear(@RequestBody CrearAlojamientoDTO dto) {
        AlojamientoDTO creado = alojamientoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<AlojamientoDTO>> obtenerTodos() {
        List<AlojamientoDTO> alojamientos = alojamientoService.obtenerTodos();
        return ResponseEntity.ok(alojamientos);
    }

    @PutMapping("/{id}/editarA")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> editarAlojamiento(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody EditarAlojamientoDTO dto) throws EditarAlojamientoException, UsuarioNoEncontradoException {

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
        }

        AlojamientoDTO alojamientoActualizado = alojamientoService.editarAlojamiento(id, dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento actualizado exitosamente", alojamientoActualizado));
    }

    @DeleteMapping("/{id}/eliminar")
    public ResponseEntity<ResponseDTO<String>> eliminarAlojamiento(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) throws EliminarAlojamientoException, UsuarioNoEncontradoException {

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
        }

        alojamientoService.eliminarAlojamiento(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento eliminado exitosamente", null));
    }

    @GetMapping("/mis-alojamientos")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarMisAlojamientos(
            @AuthenticationPrincipal UserDetails userDetails) throws ListarAlojamientosException, UsuarioNoEncontradoException {

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
        }

        List<AlojamientoDTO> alojamientos = alojamientoService.listarAlojamientosAnfitrion(String.valueOf(usuario.getId()));
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamientos obtenidos", alojamientos));
    }

    @GetMapping ("/obtenerT")

        public ResponseEntity<List<AlojamientoDTO>> obtenerTodosAlojamientos() {
            try {
                List<AlojamientoDTO> alojamientos = alojamientoService.obtenerTodos();
                return ResponseEntity.ok(alojamientos);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
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

    @GetMapping("/todos-paginado")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> obtenerTodosAlojamientosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "50") int tamanio) {

        try {
            System.out.println("📋 Obteniendo alojamientos - Pagina: " + pagina + ", Tamaño: " + tamanio);

            FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(
                    null, null, null, null, null, null, null, pagina, tamanio
            );

            PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);

            System.out.println("✅ Alojamientos encontrados: " + resultado.contenido().size());

            return ResponseEntity.ok(
                    new ResponseDTO<>(false, "Alojamientos obtenidos exitosamente", resultado)
            );

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al obtener alojamientos", null));
        }
    }

    @GetMapping("/buscar-rapida")
    public ResponseEntity<ResponseDTO<PaginacionDTO<AlojamientoDTO>>> busquedaRapida(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) Integer capacidadMin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) throws BuscarAlojamientoException {

        // Crear un filtro completo con todos los parámetros necesarios
        FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(
                ciudad,
                null,  // tipo
                null,  // precioMin
                null,  // precioMax
                capacidadMin,
                null,  // servicios
                null,  // query
                pagina,
                tamanio
        );

        PaginacionDTO<AlojamientoDTO> resultado = alojamientoService.buscarAlojamientos(filtro);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Búsqueda rápida completada", resultado));
    }

    @GetMapping("/{id}/puede-eliminarse")
    public ResponseEntity<ResponseDTO<Boolean>> puedeEliminarse(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) throws UsuarioNoEncontradoException {

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
        }

        boolean puedeEliminarse = alojamientoService.puedeEliminarse(id);
        String mensaje = puedeEliminarse ?
                "Puede eliminarse" : "No puede eliminarse (tiene reservas futuras)";

        return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, puedeEliminarse));
    }

    @PutMapping("/{id}/imagen-principal")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> marcarImagenPrincipal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestParam String imagenId) throws UsuarioNoEncontradoException {

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
        }

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
    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoDTO> obtenerAlojamientoPorId(@PathVariable Long id) {
        try {
            AlojamientoDTO alojamiento = alojamientoService.obtenerAlojamientoPorId(id);
            return ResponseEntity.ok(alojamiento);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



}
