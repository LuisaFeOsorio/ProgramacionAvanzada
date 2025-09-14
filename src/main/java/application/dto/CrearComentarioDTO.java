package application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CrearComentarioDTO(
        @NotNull Long alojamientoId,
        @NotBlank @Length(max = 500) String comentario,
        @NotNull @Min(1) @Max(5) Integer calificacion
) {
}
