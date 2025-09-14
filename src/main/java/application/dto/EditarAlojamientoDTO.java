package application.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record EditarAlojamientoDTO(
        @NotBlank @Length(max = 100) String nombre,
        @Length(max = 300) String descripcion,
        @Positive Double precioPorNoche,
        @Positive Integer capacidad,
        List<@Length(max = 300) String> imagenes,
        List<@NotBlank String> servicios
) {
}
