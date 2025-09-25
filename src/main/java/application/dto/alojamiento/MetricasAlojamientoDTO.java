package application.dto.alojamiento;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MetricasAlojamientoDTO(
        @NotBlank String alojamientoId,

        @NotNull @PositiveOrZero Long reservasTotales,

        @NotNull @DecimalMin(value = "0.0") @DecimalMax(value = "5.0")
        Double calificacionPromedio,

        LocalDate desde,
        LocalDate hasta
) {}
