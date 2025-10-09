package application.dto.reserva;

import application.model.enums.Servicio;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

public record CrearReservaDTO(
        @NotNull @Future(message = "La fecha de check-in debe ser futura")
        LocalDate checkIn,

        @NotNull @Future(message = "La fecha de check-out debe ser futura")
        LocalDate checkOut,

        @NotNull @Positive(message = "El número de huéspedes debe ser mayor a 0")
                Integer numeroHuespedes,

        @NotBlank @Length(max = 100)
                String alojamientoId,

        @NotNull
        List<Servicio> serviciosExtras
) {
}
