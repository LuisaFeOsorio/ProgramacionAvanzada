package application.controllers;

import application.dto.contrasenia.CambioContraseniaDTO;
import application.dto.usuario.*;
import application.dto.ResponseDTO;
import application.exceptions.ValueConflictException;
import application.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ✅ CREAR USUARIO
    @PostMapping
    public ResponseEntity<ResponseDTO<UsuarioDTO>> crear(@Valid @RequestBody CrearUsuarioDTO usuarioDTO) throws ValueConflictException {  // Cambiado a español
        UsuarioDTO usuarioCreado = usuarioService.crear(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(false, usuarioCreado));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UsuarioDTO>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño) {

        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos(nombre, correo, pagina, tamaño);  // Cambiado a español
        String mensaje = usuarios.isEmpty() ? "No se encontraron usuarios" : "Usuarios obtenidos exitosamente";

        return ResponseEntity.ok(new ResponseDTO<>(false, usuarios));
    }

    // ✅ OBTENER USUARIO POR ID

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> obtenerPorId(@PathVariable String id) {  // Cambiado a español
        UsuarioDTO usuarioDTO = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, usuarioDTO));
    }

    // ✅ ACTUALIZAR USUARIO

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> actualizar(
            @PathVariable String id,
            @Valid @RequestBody EditarUsuarioDTO usuarioDTO) {

        UsuarioDTO usuarioActualizado = usuarioService.actualizar(id, usuarioDTO);  // Cambiado a español
        return ResponseEntity.ok(new ResponseDTO<>(false,  usuarioActualizado));
    }


    // ✅ ELIMINAR USUARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> eliminar(@PathVariable String id) {  // Cambiado a español
        usuarioService.eliminar(id);  // Cambiado a español
        return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario eliminado exitosamente"));
    }

    // ✅ ACTIVAR/DESACTIVAR USUARIO
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> cambiarEstado(@PathVariable String id) {
        UsuarioDTO usuarioActualizado = usuarioService.cambiarEstado(id);
        String estado = usuarioActualizado.activo() ? "activado" : "desactivado";
        return ResponseEntity.ok(new ResponseDTO<>(false, usuarioActualizado));
    }

    // ✅ VOLVERSE ANFITRIÓN
    @PatchMapping("/{id}/volver-anfitrion")
    @PreAuthorize("@usuarioSecurity.esMismoUsuario(#id)")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> volverseAnfitrion(
            @PathVariable String id,
            @Valid @RequestBody VolverseAnfitrionDTO anfitrionDTO) {

        UsuarioDTO usuarioActualizado = usuarioService.volverseAnfitrion(id, anfitrionDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, usuarioActualizado));
    }

    // ✅ ACTUALIZAR INFORMACIÓN DE ANFITRIÓN
    @PutMapping("/{id}/informacion-anfitrion")
    @PreAuthorize("@usuarioSecurity.esMismoUsuario(#id)")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> actualizarInformacionAnfitrion(
            @PathVariable String id,
            @Valid @RequestBody ActualizarAnfitrionDTO anfitrionDTO) {

        UsuarioDTO usuarioActualizado = usuarioService.actualizarInformacionAnfitrion(id, anfitrionDTO);
        return ResponseEntity.ok(new ResponseDTO<>(false, usuarioActualizado));
    }

    // ✅ VERIFICAR DOCUMENTOS (solo admin)
    @PatchMapping("/{id}/verificar-documentos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> verificarDocumentos(
            @PathVariable String id,
            @RequestParam boolean verificados) {

        UsuarioDTO usuarioActualizado = usuarioService.verificarDocumentos(id, verificados);
        return ResponseEntity.ok(new ResponseDTO<>(false, usuarioActualizado));
    }
}
