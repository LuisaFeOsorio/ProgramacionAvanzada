package application.dto.reserva;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

// CrearReservaDTO.java - ACTUALIZADO
public record CrearReservaDTO(
        @NotNull(message = "La fecha de check-in es obligatoria")
        @Future(message = "La fecha de check-in debe ser futura")
        LocalDate checkIn,

        @NotNull(message = "La fecha de check-out es obligatoria")
        @Future(message = "La fecha de check-out debe ser futura")
        LocalDate checkOut,

        @Min(value = 1, message = "El número de huéspedes debe ser al menos 1")
        Integer numeroHuespedes,

        @NotNull(message = "El ID del alojamiento es obligatorio")
        Long alojamientoId,

        @NotNull(message = "El ID del usuario es obligatorio") // ← NUEVO
        Long usuarioId, // ← NUEVO CAMPO

        List<String> serviciosExtras
) {
    public CrearReservaDTO {
        if (serviciosExtras == null) {
            serviciosExtras = List.of();
        }
    }
}