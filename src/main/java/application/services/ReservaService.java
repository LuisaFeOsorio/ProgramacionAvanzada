package application.services;

import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.FiltroReservaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.dto.reserva.ReservaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {
    /** Crear reserva: valida disponibilidad, capacidad, fechas (min 1 noche, no pasado). */
    ReservaDTO crearReserva(String usuarioId, CrearReservaDTO dto);

    /** Obtener reserva por id (detalle). */
    ReservaDTO obtenerReserva(String reservaId);

    /** Cancelar reserva (validar plazo 48 horas antes del check-in según reglas). */
    void cancelarReserva(String reservaId, String quienCancelaId);

    /** Listar reservas por filtros (usuario, alojamiento, estado, rango fechas). */
    PaginacionDTO<ReservaDTO> listarReservas(FiltroReservaDTO filtro);

    /** Método para anfitrión: aprobar o rechazar una solicitud de reserva (opcional). */
    ReservaDTO aprobarReserva(String reservaId, String anfitrionId);
    ReservaDTO rechazarReserva(String reservaId, String anfitrionId);

    /** Consultar reservas de un alojamiento (para calendario). */
    List<ReservaDTO> reservasPorAlojamiento(String alojamientoId, LocalDate desde, LocalDate hasta);
}