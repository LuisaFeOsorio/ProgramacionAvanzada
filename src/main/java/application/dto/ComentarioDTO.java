package application.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record ComentarioDTO(
        @NotNull Long id,
        @NotNull Long usuarioId,
        @NotNull Long alojamientoId,
        @NotBlank @Length(max = 500) String comentario,
        @NotNull @Min(1) @Max(5) Integer calificacion,
        @NotNull @PastOrPresent LocalDate fecha
) {
}
