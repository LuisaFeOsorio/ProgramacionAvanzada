package application.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record RespuestaComentarioDTO(

        @NotBlank(message = "La respuesta no puede estar vacía")
        @Size(min = 5, max = 500, message = "La respuesta debe tener entre 5 y 500 caracteres")
        String respuesta,

        LocalDateTime fechaRespuesta

) {
    // ✅ CONSTRUCTOR PARA CREACIÓN (sin fecha)
    public RespuestaComentarioDTO(String respuesta) {
        this(respuesta, null);
    }

    // ✅ MÉTODOS DE CONVENIENCIA
    public boolean esReciente() {
        if (fechaRespuesta == null) return false;
        return fechaRespuesta.isAfter(LocalDateTime.now().minusDays(7));
    }

    public void validar() {
        if (respuesta != null && respuesta.trim().length() < 5) {
            throw new IllegalArgumentException("La respuesta debe tener al menos 5 caracteres");
        }

        if (respuesta != null && respuesta.trim().length() > 500) {
            throw new IllegalArgumentException("La respuesta no puede exceder 500 caracteres");
        }
    }

    public String getRespuestaLimpia() {
        if (respuesta == null) return null;
        return respuesta.trim();
    }
}