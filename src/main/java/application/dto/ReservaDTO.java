package application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservaDTO(
        @NotNull Long id,
        @NotNull Long usuarioId,
        @NotNull Long alojamientoId,
        @NotNull @Future LocalDate fechaInicio,
        @NotNull @Future LocalDate fechaFin,
        @NotBlank String estado // ACTIVA, CANCELADA, COMPLETADA
) {
}
