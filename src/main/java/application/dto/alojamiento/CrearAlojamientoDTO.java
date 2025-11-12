package application.dto.alojamiento;

import application.model.enums.TipoAlojamiento;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CrearAlojamientoDTO(

        // Datos principales
        @NotBlank @Length(min = 5, max = 200) String nombre,
        @NotBlank @Length(min = 10, max = 1000) String descripcion,
        @NotNull TipoAlojamiento tipo,

        // Ubicación
        @NotBlank @Length(max = 100) String ciudad,
        @NotBlank @Length(max = 100) String pais,
        @NotBlank @Length(max = 500) String direccion,

        // Precio y capacidad
        @NotNull @Positive Double precioPorNoche,
        @NotNull @Positive Integer capacidadMaxima,
        @NotNull @Min(1) @Max(20) Integer numeroHabitaciones,
        @NotNull @Min(1) @Max(10) Integer numeroBanos,

        // Servicios e imágenes
        List<@NotBlank String> servicios,
        List<@Length(max = 300) String> imagenes,

        // Relación obligatoria con el anfitrión
        @NotNull Long anfitrionId

) {
}


