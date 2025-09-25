package application.dto.reserva;


import java.time.LocalDate;

public record FiltroReservaDTO(
        String alojamientoId,   // ID del alojamiento al que pertenece la reserva
        String usuarioId,       // ID del usuario que hizo la reserva
        LocalDate fechaInicio,  // Fecha mínima de la reserva
        LocalDate fechaFin,     // Fecha máxima de la reserva
        String estado           // Estado de la reserva (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA)
) {}

