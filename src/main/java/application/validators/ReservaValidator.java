package application.validators;

import application.dto.reserva.CrearReservaDTO;
import application.exceptions.reserva.NumeroHuespedes;
import application.exceptions.reserva.ReservaFechasNulasException;
import application.exceptions.reserva.TiempoReserva;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ReservaValidator {

    // ✅ VALIDACIONES COMPLETAS DE RESERVA
    public void validarCreacionReserva(CrearReservaDTO dto) throws ReservaFechasNulasException, TiempoReserva {
        validarFechasNoNulas(dto);
        validarFechasFuturas(dto);
        validarRangoFechas(dto);
        validarMinimoUnaNoche(dto);
        validarMaximoTreintaNoches(dto);
    }

    private void validarFechasNoNulas(CrearReservaDTO dto) throws ReservaFechasNulasException {
        if (dto.checkIn() == null) {
            throw new ReservaFechasNulasException("La fecha de check-in es requerida");
        }
        if (dto.checkOut() == null) {
            throw new ReservaFechasNulasException("La fecha de check-out es requerida");
        }
    }

    private void validarFechasFuturas(CrearReservaDTO dto) throws ReservaFechasNulasException {
        if (dto.checkIn().isBefore(LocalDate.now())) {
            throw new ReservaFechasNulasException("No se pueden reservar fechas pasadas");
        }
        if (dto.checkOut().isBefore(LocalDate.now())) {
            throw new ReservaFechasNulasException("No se pueden reservar fechas pasadas");
        }
    }

    private void validarRangoFechas(CrearReservaDTO dto) throws ReservaFechasNulasException {
        if (!dto.checkOut().isAfter(dto.checkIn())) {
            throw new ReservaFechasNulasException("El check-out debe ser posterior al check-in");
        }
    }

    private void validarMinimoUnaNoche(CrearReservaDTO dto) throws TiempoReserva {
        long noches = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        if (noches < 1) {
            throw new TiempoReserva("La reserva debe ser de al menos 1 noche");
        }
    }

    private void validarMaximoTreintaNoches(CrearReservaDTO dto) throws TiempoReserva {
        long noches = ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        if (noches > 30) {
            throw new TiempoReserva("No se pueden reservar más de 30 noches");
        }
    }

    // ✅ VALIDACIONES ADICIONALES
    public void validarCapacidad(Integer capacidadMaxima, Integer numeroHuespedes) throws NumeroHuespedes {
        if (numeroHuespedes > capacidadMaxima) {
            throw new NumeroHuespedes(
                    "Número de huéspedes (" + numeroHuespedes + ") excede la capacidad máxima (" + capacidadMaxima + ")"
            );
        }
    }

    public void validarCancelacion(LocalDate checkIn, String quienCancela) throws TiempoReserva {
        LocalDate limiteCancelacion = checkIn.minusDays(2); // 48 horas antes
        if (LocalDate.now().isAfter(limiteCancelacion)) {
            throw new TiempoReserva(
                    "Solo se puede cancelar hasta 48 horas antes del check-in"
            );
        }
    }
}
