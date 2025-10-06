package application.dto.contrasenia;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CambioContraseniaDTO(
        @NotBlank @Length(min = 6, max = 50) String contraseniaActual,
        @NotBlank @Length(min = 6, max = 50) String nuevaContrasenia) {
}
