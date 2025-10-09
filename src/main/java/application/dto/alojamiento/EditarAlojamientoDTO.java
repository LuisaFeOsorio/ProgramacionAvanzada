package application.dto.alojamiento;

import jakarta.validation.constraints.*;
import java.util.List;

public record EditarAlojamientoDTO(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es requerida")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String descripcion,

        @NotNull(message = "El precio por noche es requerido")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio por noche debe ser mayor a 0")
        @DecimalMax(value = "10000.0", message = "El precio por noche no puede exceder $10,000")
        Double precioPorNoche,

        @NotNull(message = "La capacidad máxima es requerida")
        @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
        @Max(value = 50, message = "La capacidad máxima no puede exceder 50 personas")
        Integer capacidadMaxima,

        @NotNull(message = "El número de habitaciones es requerido")
        @Min(value = 1, message = "Debe tener al menos 1 habitación")
        @Max(value = 20, message = "No puede tener más de 20 habitaciones")
        Integer numeroHabitaciones,

        @NotNull(message = "El número de baños es requerido")
        @Min(value = 1, message = "Debe tener al menos 1 baño")
        @Max(value = 10, message = "No puede tener más de 10 baños")
        Integer numeroBanos,

        @NotNull(message = "Los servicios no pueden ser nulos")
        List<@NotBlank(message = "El servicio no puede estar vacío") String> servicios
) {}