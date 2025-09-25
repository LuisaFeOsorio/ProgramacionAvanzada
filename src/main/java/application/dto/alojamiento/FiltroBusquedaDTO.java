package application.dto.alojamiento;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record FiltroBusquedaDTO(

        @Size(max = 100) String ciudad,
        @FutureOrPresent LocalDate fechaInicio,
        @FutureOrPresent LocalDate fechaFin,
        @PositiveOrZero Double precioMin,
        @PositiveOrZero Double precioMax)
{}