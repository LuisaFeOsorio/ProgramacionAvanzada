package application.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RespuestaComentarioDTO(

        @NotBlank(message = "El texto de la respuesta no puede estar vacío")
        @Size(max = 500, message = "La respuesta no puede superar los 500 caracteres")
        String texto

) {}
