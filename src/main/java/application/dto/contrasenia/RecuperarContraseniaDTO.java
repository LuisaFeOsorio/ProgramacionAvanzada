package application.dto.contrasenia;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperarContraseniaDTO(
        @NotBlank @Email String correo) {
}
