package application.services.reserva;

import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.FiltroReservaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.dto.reserva.ReservaDTO;
import application.exceptions.reserva.ReservaNoCanceladaException;
import application.exceptions.reserva.ReservaNoCreadaException;
import application.exceptions.reserva.ReservasNoObtenidasException;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {

    ReservaDTO crearReserva(CrearReservaDTO dto);

    List<ReservaDTO> findByUsuarioId(Long usuarioId);

    ReservaDTO obtenerReserva(String reservaId) throws ReservasNoObtenidasException;

    ReservaDTO aprobarReserva(String reservaId, String anfitrionId);

    ReservaDTO rechazarReserva(String reservaId, String anfitrionId);

    PaginacionDTO<ReservaDTO> listarReservas(FiltroReservaDTO filtro);

    PaginacionDTO<ReservaDTO> reservasPorUsuario(String usuarioId, int pagina, int tamanio);

    PaginacionDTO<ReservaDTO> reservasPorAnfitrion(String anfitrionId, int pagina, int tamanio);

    List<ReservaDTO> reservasPorAlojamiento(String alojamientoId, LocalDate desde, LocalDate hasta);

    List<ReservaDTO> reservasParaCalendario(String alojamientoId, LocalDate desde, LocalDate hasta);

    void actualizarEstadosReservas();

    ReservaDTO cancelarReserva(Long id);
}