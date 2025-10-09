package application.dto.comentario;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ComentarioDTO(
        Long id,

        @NotBlank @Size(min = 10, max = 1000)
        String contenido,

        @NotNull @Min(1) @Max(5)
        Integer calificacion,

        @Size(max = 1000)
        String respuesta,

        @NotNull
        LocalDateTime fechaCreacion,

        LocalDateTime fechaRespuesta,

        @NotNull
        Boolean activo,

        // ✅ INFORMACIÓN DEL USUARIO
        @NotBlank
        String usuarioNombre,

        String usuarioFoto,

        @NotNull
        Long usuarioId,

        // ✅ INFORMACIÓN DEL ALOJAMIENTO
        @NotBlank
        String alojamientoNombre,

        @NotNull
        Long alojamientoId,

        // ✅ INFORMACIÓN DE LA ESTADÍA
        @NotNull
        LocalDate fechaEstadia,

        // ✅ CAMPOS CALCULADOS (AGREGAR ESTOS)
        String tiempoTranscurrido,
        Boolean tieneRespuesta

) {
    // ✅ CONSTRUCTOR COMPACTO PARA CAMPOS CALCULADOS
    public ComentarioDTO {
        // Validar e inicializar campos calculados si vienen nulos
        if (tieneRespuesta == null) {
            tieneRespuesta = respuesta != null && !respuesta.trim().isEmpty();
        }

        if (tiempoTranscurrido == null && fechaCreacion != null) {
            tiempoTranscurrido = calcularTiempoTranscurrido(fechaCreacion);
        }
    }

    // ✅ MÉTODO PRIVADO PARA CALCULAR TIEMPO TRANSCURRIDO
    private static String calcularTiempoTranscurrido(LocalDateTime fecha) {
        if (fecha == null) return "";

        java.time.Duration duration = java.time.Duration.between(fecha, java.time.LocalDateTime.now());
        long minutos = duration.toMinutes();
        long horas = duration.toHours();
        long dias = duration.toDays();

        if (minutos < 1) return "Ahora mismo";
        if (minutos < 60) return minutos + " minuto" + (minutos != 1 ? "s" : "");
        if (horas < 24) return horas + " hora" + (horas != 1 ? "s" : "");
        if (dias < 30) return dias + " día" + (dias != 1 ? "s" : "");

        long meses = dias / 30;
        if (meses < 12) return meses + " mes" + (meses != 1 ? "es" : "");

        long años = meses / 12;
        return años + " año" + (años != 1 ? "s" : "");
    }
}