package application.controllers;

import application.dto.usuario.*;
import application.dto.ResponseDTO;
import application.dto.contrasenia.CambioContraseniaDTO;
import application.exceptions.ValueConflictException;
import application.exceptions.usuario.EmailEnUsoException;
import application.exceptions.usuario.UsuarioNoEncontradoException;
import application.services.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ResponseDTO<UsuarioDTO>> crearUsuario(
            @Valid @RequestBody CrearUsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO usuarioCreado = usuarioService.crear(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO<>(false, "Usuario creado exitosamente", usuarioCreado));

        } catch (EmailEnUsoException | ValueConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error interno del servidor", null));
        }
    }

    // ✅ OBTENER TODOS LOS USUARIOS (SOLO ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<UsuarioDTO>>> obtenerTodosUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos(nombre, email, pagina, tamanio);
        String mensaje = usuarios.isEmpty() ? "No se encontraron usuarios" : "Usuarios obtenidos exitosamente";

        return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, usuarios));
    }

    // ✅ OBTENER USUARIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> obtenerUsuarioPorId(@PathVariable String id) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario obtenido exitosamente", usuario));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ ACTUALIZAR USUARIO (USUARIO PROPIETARIO O ADMIN) - CORREGIDO
    @PutMapping("/{id}")
    @PreAuthorize("@usuarioSecurity.esMismoUsuario(#id) or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> actualizarUsuario(
            @PathVariable String id,
            @Valid @RequestBody EditarUsuarioDTO usuarioDTO) {

        System.out.println("✏️ === ACTUALIZAR USUARIO ===");
        System.out.println("📥 ID del path: " + id);
        System.out.println("📥 DTO recibido - Nombre: " + usuarioDTO.nombre() + ", Email: " + usuarioDTO.email());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Authentication: " + auth);
        System.out.println("👤 Principal: " + (auth != null ? auth.getPrincipal() : "null"));

        try {
            UsuarioDTO usuarioActualizado = usuarioService.actualizar(id, usuarioDTO);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario actualizado exitosamente", usuarioActualizado));
        } catch (EmailEnUsoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (Exception e) {
            System.out.println("❌ Error en actualizarUsuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error interno: " + e.getMessage(), null));
        }
    }

    // ✅ ELIMINAR USUARIO (SOLO ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<String>> eliminarUsuario(@PathVariable String id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario eliminado exitosamente", null));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ CAMBIAR CONTRASEÑA (USUARIO PROPIETARIO)
    @PostMapping("/{id}/cambiar-contrasenia")
    @PreAuthorize("@usuarioSecurity.esMismoUsuario(#id)")
    public ResponseEntity<ResponseDTO<String>> cambiarContrasenia(
            @PathVariable String id,
            @Valid @RequestBody CambioContraseniaDTO cambioContraseniaDTO) {

        try {
            usuarioService.cambiarContrasenia(id, cambioContraseniaDTO);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Contraseña cambiada exitosamente", null));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ ACTIVAR/DESACTIVAR USUARIO (SOLO ADMIN)
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> cambiarEstadoUsuario(@PathVariable String id) {
        try {
            UsuarioDTO usuarioActualizado = usuarioService.cambiarEstado(id);
            String estado = usuarioActualizado.activo() ? "activado" : "desactivado";
            return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario " + estado + " exitosamente", usuarioActualizado));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ VOLVERSE ANFITRIÓN (USUARIO PROPIETARIO)
    @PatchMapping("/{id}/volver-anfitrion")
    @PreAuthorize("@usuarioSecurity.esMismoUsuario(#id)")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> volverseAnfitrion(
            @PathVariable String id,
            @Valid @RequestBody VolverseAnfitrionDTO anfitrionDTO) {

        try {
            UsuarioDTO usuarioActualizado = usuarioService.volverseAnfitrion(id, anfitrionDTO);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Ahora eres anfitrión", usuarioActualizado));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ ACTUALIZAR INFORMACIÓN DE ANFITRIÓN (ANFITRIÓN PROPIETARIO)
    @PutMapping("/{id}/informacion-anfitrion")
    @PreAuthorize("@usuarioSecurity.esMismoUsuario(#id) and @usuarioSecurity.esAnfitrion(#id)")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> actualizarInformacionAnfitrion(
            @PathVariable String id,
            @Valid @RequestBody ActualizarAnfitrionDTO anfitrionDTO) {

        try {
            UsuarioDTO usuarioActualizado = usuarioService.actualizarInformacionAnfitrion(id, anfitrionDTO);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Información de anfitrión actualizada", usuarioActualizado));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ VERIFICAR DOCUMENTOS (SOLO ADMIN)
    @PatchMapping("/{id}/verificar-documentos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> verificarDocumentos(
            @PathVariable String id,
            @RequestParam boolean verificados) {

        try {
            UsuarioDTO usuarioActualizado = usuarioService.verificarDocumentos(id, verificados);
            String estado = verificados ? "verificados" : "no verificados";
            return ResponseEntity.ok(new ResponseDTO<>(false, "Documentos " + estado, usuarioActualizado));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ OBTENER ANFITRIONES (PÚBLICO)
    @GetMapping("/anfitriones")
    public ResponseEntity<ResponseDTO<List<UsuarioDTO>>> obtenerAnfitriones(
            @RequestParam(required = false) Boolean verificados) {

        boolean soloVerificados = Boolean.TRUE.equals(verificados);
        List<UsuarioDTO> anfitriones = usuarioService.obtenerAnfitriones(soloVerificados);

        String mensaje = soloVerificados ?
                "Anfitriones verificados obtenidos" : "Anfitriones obtenidos";

        return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, anfitriones));
    }

    // ✅ OBTENER MI PERFIL (USUARIO AUTENTICADO) - CORREGIDO
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> obtenerMiPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("🔍 === OBTENER MI PERFIL ===");
            System.out.println("UserDetails: " + userDetails);

            if (userDetails == null) {
                System.out.println("❌ UserDetails is null - authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO<>(true, "No autenticado", null));
            }

            String email = userDetails.getUsername();
            System.out.println("✅ Authenticated user: " + email);
            System.out.println("✅ Authorities: " + userDetails.getAuthorities());

            UsuarioDTO usuario = usuarioService.obtenerPorEmail(email);

            return ResponseEntity.ok(new ResponseDTO<>(false, "Perfil obtenido", usuario));

        } catch (Exception e) {
            System.out.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error interno", null));
        }
    }

    // ✅ VERIFICAR SI ES ANFITRIÓN
    @GetMapping("/{id}/es-anfitrion")
    public ResponseEntity<ResponseDTO<Boolean>> esAnfitrion(@PathVariable String id) {
        try {
            boolean esAnfitrion = usuarioService.esAnfitrion(id);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Verificación completada", esAnfitrion));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        }
    }

    // ✅ ENDPOINT TEMPORAL PARA ACTUALIZACIÓN (SIN SEGURIDAD)
    @PutMapping("/{id}/temp")

    public ResponseEntity<ResponseDTO<UsuarioDTO>> actualizarUsuarioTemp(
            @PathVariable String id,
            @RequestBody EditarUsuarioDTO usuarioDTO) {

        System.out.println("🎯 === ACTUALIZACIÓN TEMPORAL - INICIANDO ===");
        System.out.println("📥 ID recibido: " + id);
        System.out.println("📥 DTO - Nombre: " + usuarioDTO.nombre());
        System.out.println("📥 DTO - Email: " + usuarioDTO.email());
        System.out.println("📥 DTO - Teléfono: " + usuarioDTO.telefono());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Usuario autenticado: " + (auth != null ? auth.getName() : "null"));

        try {
            System.out.println("🔄 Llamando a usuarioService.actualizar()...");
            UsuarioDTO usuarioActualizado = usuarioService.actualizar(id, usuarioDTO);
            System.out.println("✅ Usuario actualizado exitosamente: " + usuarioActualizado.nombre());
            return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario actualizado exitosamente (temp)", usuarioActualizado));

        } catch (EmailEnUsoException e) {
            System.out.println("❌ Email en uso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (Exception e) {
            System.out.println("💥 ERROR CRÍTICO en actualización: " + e.getClass().getSimpleName());
            System.out.println("💥 Mensaje: " + e.getMessage());
            e.printStackTrace(); // ⭐⭐ ESTO ES CRÍTICO ⭐⭐
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error: " + e.getMessage(), null));
        }
    }
}