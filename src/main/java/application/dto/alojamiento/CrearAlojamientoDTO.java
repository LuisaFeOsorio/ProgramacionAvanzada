package application.dto.alojamiento;

import application.model.enums.TipoAlojamiento;
import jakarta.validation.constraints.*;

import java.util.List;

public record CrearAlojamientoDTO(

        // ✅ INFORMACIÓN BÁSICA
        @NotBlank(message = "El nombre del alojamiento es requerido")
        @Size(min = 5, max = 200, message = "El nombre debe tener entre 5 y 200 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es requerida")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String descripcion,

        @NotNull(message = "El tipo de alojamiento es requerido")
        TipoAlojamiento tipo,

        // ✅ UBICACIÓN
        @NotBlank(message = "La ciudad es requerida")
        @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
        String ciudad,

        @NotBlank(message = "El país es requerido")
        @Size(max = 100, message = "El país no puede exceder 100 caracteres")
        String pais,

        @NotBlank(message = "La dirección es requerida")
        @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
        String direccion,

        // ✅ PRECIO Y CAPACIDAD
        @NotNull(message = "El precio por noche es requerido")
        @Positive(message = "El precio por noche debe ser mayor a 0")
        @DecimalMax(value = "10000.0", message = "El precio por noche no puede exceder $10,000")
        Double precioPorNoche,

        @NotNull(message = "La capacidad máxima es requerida")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        @Max(value = 50, message = "La capacidad no puede exceder 50 huéspedes")
        Integer capacidadMaxima,

        // ✅ CARACTERÍSTICAS
        @Min(value = 1, message = "El número de habitaciones debe ser al menos 1")
        @Max(value = 20, message = "El número de habitaciones no puede exceder 20")
        Integer numeroHabitaciones,

        @Min(value = 1, message = "El número de baños debe ser al menos 1")
        @Max(value = 10, message = "El número de baños no puede exceder 10")
        Integer numeroBanos,

        // ✅ SERVICIOS E IMÁGENES
        List<String> servicios,

        List<@Pattern(regexp = "^(http|https)://.*", message = "Las URLs de imágenes deben ser válidas") String> imagenes

) {

}