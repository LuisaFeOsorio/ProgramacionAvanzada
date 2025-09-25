package application.dto.contraseña;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ResetContraseniaDTO(
        @NotBlank String token,
        @NotBlank @Length(min = 6, max = 50) String nuevaContrasena
) {
}
