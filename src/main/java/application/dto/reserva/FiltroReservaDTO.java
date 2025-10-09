package application.dto.reserva;


import java.time.LocalDate;

public record FiltroReservaDTO(
        int pagina,
        int tamanio,
        String alojamientoId,
        String anfitrionId,
        String usuarioId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado
) {}

