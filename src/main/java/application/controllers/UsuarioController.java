package application.controllers;

import application.dto.ResponseDTO;
import application.dto.contrasenia.CambioContraseniaDTO;
import application.dto.usuario.*;
import application.exceptions.usuario.EmailEnUsoException;
import application.exceptions.usuario.UsuarioNoEncontradoException;
import application.mappers.UsuarioMapper;
import application.model.Usuario;
import application.model.enums.Role;
import application.repositories.UsuarioRepository;
import application.services.impl.ImageServiceImpl;
import application.services.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    Role role;
    private final ImageServiceImpl imagenService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    @PostMapping(value = "/registro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<ResponseDTO<UsuarioDTO>> crearUsuario(
            @RequestPart("nombre") String nombre,
            @RequestPart("apellido") String apellido,
            @RequestPart("email") String email,
            @RequestPart(value = "telefono", required = false) String telefono,
            @RequestPart("fechaNacimiento") String fechaNacimientoStr,
            @RequestPart("contrasenia") String contrasenia,
            @RequestPart(value = "rol", required = false) String rol,
            @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil) {

        try {

            LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

            String fotoPerfilUrl = null;
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                fotoPerfilUrl = imagenService.upload(fotoPerfil).toString();
            }

            Role role;
            if (rol != null && rol.equalsIgnoreCase("ANFITRION")) {
                role = Role.ANFITRION;
            } else {
                role = Role.USUARIO;
            }

            CrearUsuarioDTO usuarioDTO = new CrearUsuarioDTO(
                    nombre + " " + apellido,
                    email,
                    telefono != null ? telefono : "",
                    contrasenia,
                    fotoPerfilUrl != null ? fotoPerfilUrl : "",
                    fechaNacimiento,
                    role
            );

            UsuarioDTO usuarioCreado = usuarioService.crear(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO<>(false, "Usuario creado exitosamente", usuarioCreado));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al procesar la imagen: " + e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error interno del servidor: " + e.getMessage(), null));
        }
    }

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

//estamos usando este
    @PutMapping("/{id}/editar")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody EditarUsuarioDTO dto) {

        System.out.println("📥 [PUT] Actualizando usuario ID=" + id);
        System.out.println("📦 DTO recibido: " + dto);

        try {
            UsuarioDTO actualizado = usuarioService.actualizarUsuario(id, dto);

            return ResponseEntity.ok(
                    new ResponseDTO<>(false, "Usuario actualizado correctamente", actualizado)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al actualizar usuario: " + e.getMessage(), null));
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
            UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(Long.valueOf(id), usuarioDTO);
            System.out.println("✅ Usuario actualizado exitosamente: " + usuarioActualizado.nombre());
            return ResponseEntity.ok(new ResponseDTO<>(false, "Usuario actualizado exitosamente (temp)", usuarioActualizado));

        } catch (Exception e) {
            System.out.println("💥 ERROR CRÍTICO en actualización: " + e.getClass().getSimpleName());
            System.out.println("💥 Mensaje: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error: " + e.getMessage(), null));
        }
    }
}