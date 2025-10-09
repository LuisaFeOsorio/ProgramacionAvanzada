package application.controllers;

import application.dto.ResponseDTO;
import application.services.contrasenia.ContraseniaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contrasenia")
@RequiredArgsConstructor
public class ContraseniaController {

    private final ContraseniaService contraseniaService;

    // ✅ SOLICITAR CÓDIGO DE RECUPERACIÓN
    @PostMapping("/solicitar-codigo")
    public ResponseEntity<ResponseDTO<String>> solicitarCodigoRecuperacion(
            @Valid @RequestBody SolicitarCodigoDTO dto) {

        contraseniaService.solicitarCodigoRecuperacion(dto.email());
        return ResponseEntity.ok(new ResponseDTO<>(false, "Código de recuperación enviado al email", null));
    }

    // ✅ VERIFICAR CÓDIGO DE RECUPERACIÓN
    @PostMapping("/verificar-codigo")
    public ResponseEntity<ResponseDTO<Boolean>> verificarCodigo(
            @Valid @RequestBody VerificarCodigoDTO dto) {

        boolean codigoValido = contraseniaService.verificarCodigo(dto.email(), dto.codigo());
        String mensaje = codigoValido ? "Código válido" : "Código inválido o expirado";

        return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, codigoValido));
    }

    // ✅ RESTABLECER CONTRASEÑA CON CÓDIGO
    @PostMapping("/restablecer")
    public ResponseEntity<ResponseDTO<String>> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaDTO dto) {

        contraseniaService.restablecerContrasena(dto.email(), dto.codigo(), dto.nuevaContrasenia());
        return ResponseEntity.ok(new ResponseDTO<>(false, "Contraseña restablecida exitosamente", null));
    }

    // ✅ MANEJO DE EXCEPCIONES ESPECÍFICO
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(true, ex.getMessage(), null));
    }

    // 🔧 DTOs PARA LAS SOLICITUDES

    public record SolicitarCodigoDTO(
            @jakarta.validation.constraints.Email(message = "El formato del email no es válido")
            @jakarta.validation.constraints.NotBlank(message = "El email es requerido")
            String email
    ) {}

    public record VerificarCodigoDTO(
            @jakarta.validation.constraints.Email(message = "El formato del email no es válido")
            @jakarta.validation.constraints.NotBlank(message = "El email es requerido")
            String email,

            @jakarta.validation.constraints.NotBlank(message = "El código es requerido")
            @jakarta.validation.constraints.Pattern(regexp = "\\d{6}", message = "El código debe tener 6 dígitos")
            String codigo
    ) {}

    public record RestablecerContrasenaDTO(
            @jakarta.validation.constraints.Email(message = "El formato del email no es válido")
            @jakarta.validation.constraints.NotBlank(message = "El email es requerido")
            String email,

            @jakarta.validation.constraints.NotBlank(message = "El código es requerido")
            @jakarta.validation.constraints.Pattern(regexp = "\\d{6}", message = "El código debe tener 6 dígitos")
            String codigo,

            @jakarta.validation.constraints.NotBlank(message = "La nueva contraseña es requerida")
            @jakarta.validation.constraints.Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
            String nuevaContrasenia
    ) {}
}