package application.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CambioContraseñaDTO(
        @NotBlank @Length(min = 6, max = 50) String contrasenaActual,
        @NotBlank @Length(min = 6, max = 50) String nuevaContrasena) {
}
