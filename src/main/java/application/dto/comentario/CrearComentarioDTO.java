package application.dto.comentario;

import jakarta.validation.constraints.*;

public record CrearComentarioDTO(
        @NotBlank(message = "El contenido del comentario es requerido")
        @Size(min = 10, max = 1000, message = "El comentario debe tener entre 10 y 1000 caracteres")
        String contenido,

        @NotNull(message = "La calificación es requerida")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        Integer calificacion
) {

}