package application.dto.comentario;

import jakarta.validation.constraints.Size;

public record ActualizarComentarioDTO(
        @Size(min = 10, max = 1000, message = "El comentario debe tener entre 10 y 1000 caracteres")
        String contenido,

        @Size(max = 500, message = "La respuesta no puede exceder 500 caracteres")
        String respuesta
) {

}