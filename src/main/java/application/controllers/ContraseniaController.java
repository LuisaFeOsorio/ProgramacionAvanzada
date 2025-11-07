package application.controllers;

import application.dto.ResponseDTO;
import application.services.contrasenia.ContraseniaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contrasenia")
@RequiredArgsConstructor
public class ContraseniaController {

    private final ContraseniaService contraseniaService;

    // ✅ SOLICITAR CÓDIGO DE RECUPERACIÓN
    @PostMapping("/solicitar-codigo")
    public ResponseEntity<ResponseDTO<String>> solicitarCodigoRecuperacion(
            @Valid @RequestBody SolicitarCodigoDTO dto) {

        try {
            contraseniaService.solicitarCodigoRecuperacion(dto.email());
            return ResponseEntity.ok(new ResponseDTO<>(false, "Código de recuperación enviado al email", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al enviar el código de recuperación", null));
        }
    }

    // ✅ VERIFICAR CÓDIGO DE RECUPERACIÓN
    @PostMapping("/verificar-codigo")
    public ResponseEntity<ResponseDTO<Boolean>> verificarCodigo(
            @Valid @RequestBody VerificarCodigoDTO dto) {

        try {
            boolean codigoValido = contraseniaService.verificarCodigo(dto.email(), dto.codigo());
            String mensaje = codigoValido ? "Código válido" : "Código inválido";

            return ResponseEntity.ok(new ResponseDTO<>(false, mensaje, codigoValido));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(true, e.getMessage(), false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al verificar el código", false));
        }
    }

    // ✅ RESTABLECER CONTRASEÑA CON CÓDIGO
    @PostMapping("/restablecer")
    public ResponseEntity<ResponseDTO<String>> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaDTO dto) {

        try {
            contraseniaService.restablecerContrasena(dto.email(), dto.codigo(), dto.nuevaContrasenia());
            return ResponseEntity.ok(new ResponseDTO<>(false, "¡Contraseña restablecida exitosamente!", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(true, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al restablecer la contraseña", null));
        }
    }

    // ✅ DTOs
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
            @jakarta.validation.constraints.Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            String nuevaContrasenia
    ) {}
}