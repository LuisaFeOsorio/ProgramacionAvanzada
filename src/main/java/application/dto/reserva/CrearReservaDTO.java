package application.dto.reserva;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CrearReservaDTO(
        @NotNull Long alojamientoId,
        @NotNull @Future LocalDate fechaInicio,
        @NotNull @Future LocalDate fechaFin
) {
}
