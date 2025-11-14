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


@PostMapping("/crear/{Id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> crearAlojamiento(
            @PathVariable Long anfitrionId,
            @Valid @RequestBody CrearAlojamientoDTO dto) {

        System.out.println("🏠 === INICIANDO CREACIÓN DE ALOJAMIENTO ===");
        System.out.println("🧑 ID del anfitrión recibido: " + anfitrionId);

        try {
            Usuario usuario = usuarioService.findById(anfitrionId)
                    .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + anfitrionId));

            System.out.println("✅ Usuario encontrado: " + usuario.getNombre());
            AlojamientoDTO alojamientoCreado = alojamientoService.crearAlojamiento(String.valueOf(anfitrionId), dto);

            System.out.println("🎉 Alojamiento creado exitosamente con ID: " + alojamientoCreado.id());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO<>(false, "Alojamiento creado exitosamente", alojamientoCreado));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al crear alojamiento: " + e.getMessage(), null));
        }
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

    //nuevo


    @GetMapping("/mis-alojamientos/{idAnfitrion}")
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listarMisAlojamientos(
            @PathVariable Long idAnfitrion) {
        System.out.println("\n🎯 [GET] /api/alojamientos/mis-alojamientos/" + idAnfitrion);

        try {
            Usuario usuario = usuarioService.findById(idAnfitrion)
                    .orElse(null);

            if (usuario == null) {
                System.err.println("⚠️ Usuario no encontrado con ID: " + idAnfitrion);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO<>(true, "Usuario no encontrado", null));
            }

            System.out.println("✅ Usuario encontrado: " + usuario.getNombre() + " (ID: " + usuario.getId() + ")");

            // 👉 Usa directamente el ID como Long, no como String
            List<AlojamientoDTO> alojamientos = alojamientoService.listarAlojamientosAnfitrion(usuario.getId().toString());

            if (alojamientos == null || alojamientos.isEmpty()) {
                System.out.println("⚠️ No se encontraron alojamientos para el anfitrión con ID: " + usuario.getId());
                return ResponseEntity.ok(new ResponseDTO<>(false, "No se encontraron alojamientos", List.of()));
            }

            System.out.println("📦 Alojamientos encontrados: " + alojamientos.size());
            alojamientos.forEach(a ->
                    System.out.println("   🏠 " + a.nombre() + " - " + a.ciudad())
            );

            return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamientos obtenidos correctamente", alojamientos));

        } catch (Exception e) {
            System.err.println("💥 Error en listarMisAlojamientos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al listar alojamientos: " + e.getMessage(), null));
        }
    }


    @GetMapping("/obtenerT")

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

    @PutMapping("/{id}/eliminar")
    public ResponseEntity<ResponseDTO<String>> eliminarAlojamiento(
            @PathVariable Long id) {

        try {
            boolean puedeEliminarse = alojamientoService.puedeEliminarse(String.valueOf(id));

            if (!puedeEliminarse) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO<>(true,
                                "No puede eliminarse: tiene reservas futuras",
                                null));
            }

            alojamientoService.marcarComoInactivo(id);

            return ResponseEntity.ok(
                    new ResponseDTO<>(false, "Alojamiento marcado como INACTIVO", "OK")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true,
                            "Error eliminando alojamiento: " + e.getMessage(),
                            null));
        }
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

    //nuevoo
    @PutMapping("/editar/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> editarAlojamiento(
            @PathVariable Long id,
            @Valid @RequestBody CrearAlojamientoDTO dto) {

        System.out.println("\n========== 🏠 [INICIO EDICIÓN DE ALOJAMIENTO] ==========");
        System.out.println("📌 ID recibido: " + id);
        System.out.println("📦 Datos recibidos:");
        System.out.println("   - Nombre: " + dto.nombre());
        System.out.println("   - Descripción: " + dto.descripcion());
        System.out.println("   - Ciudad: " + dto.ciudad());
        System.out.println("   - País: " + dto.pais());
        System.out.println("   - Precio por noche: " + dto.precioPorNoche());
        System.out.println("   - Servicios: " + dto.servicios());
        System.out.println("   - Imágenes: " + dto.imagenes());

        try {
            AlojamientoDTO alojamientoActualizado = alojamientoService.editarAlojamiento(id.toString(), dto);
            System.out.println("✅ Alojamiento actualizado correctamente con ID: " + alojamientoActualizado.id());
            System.out.println("========== ✅ [FIN EDICIÓN ALOJAMIENTO] ==========\n");

            return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento actualizado exitosamente", alojamientoActualizado));

        } catch (Exception e) {
            System.out.println("❌ Error durante la edición del alojamiento:");
            e.printStackTrace();
            System.out.println("========== ❌ [FIN CON ERROR] ==========\n");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al actualizar el alojamiento: " + e.getMessage(), null));
        }
    }

    //nuevoo
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> obtenerAlojamientoPorId(@PathVariable Long id) {
        System.out.println("\n========== 🏠 [INICIO CONSULTA ALOJAMIENTO POR ID] ==========");
        System.out.println("📌 ID recibido: " + id);

        try {
            AlojamientoDTO alojamientoDTO = alojamientoService.obtenerAlojamientoPorId(id);

            System.out.println("✅ Alojamiento encontrado:");
            System.out.println("   - Nombre: " + alojamientoDTO.nombre());
            System.out.println("   - Ciudad: " + alojamientoDTO.ciudad());
            System.out.println("   - Precio por noche: " + alojamientoDTO.precioPorNoche());
            System.out.println("========== ✅ [FIN CONSULTA ALOJAMIENTO] ==========\n");

            return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento obtenido correctamente", alojamientoDTO));

        } catch (Exception e) {
            System.out.println("❌ Error al obtener alojamiento:");
            e.printStackTrace();
            System.out.println("========== ❌ [FIN CON ERROR] ==========\n");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "Error al obtener alojamiento: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/puede-eliminarse")
    public ResponseEntity<ResponseDTO<Boolean>> puedeEliminarse(@PathVariable String id) {

        try {
            boolean puedeEliminarse = alojamientoService.puedeEliminarse(id);

            String mensaje = puedeEliminarse
                    ? "Puede eliminarse"
                    : "No puede eliminarse (tiene reservas futuras)";

            return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, puedeEliminarse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error interno: " + e.getMessage(), null));
        }
    }

}
