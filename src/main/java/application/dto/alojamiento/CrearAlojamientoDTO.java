package application.dto.alojamiento;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CrearAlojamientoDTO(
        @NotBlank @Length(max = 100) String nombre,
        @NotBlank @Length(max = 300) String descripcion,
        @NotBlank String ciudad,
        @NotNull @Positive Double precioPorNoche,
        @NotNull @Positive Integer capacidad,
        List<@Length(max = 300) String> imagenes,
        List<@NotBlank String> servicios
) {
}
