package application.services;

import application.dto.alojamiento.DateRangeDTO;

import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadService {
    /** Verifica si un alojamiento está disponible en un rango de fechas (no solapamiento). */
    boolean estaDisponible(String alojamientoId, LocalDate checkIn, LocalDate checkOut);

    /** Lista de periodos ocupados (reservas confirmadas) para mostrar en calendario. */
    List<DateRangeDTO> obtenerPeriodosOcupados(String alojamientoId, LocalDate desde, LocalDate hasta);

    /** Bloquear fechas manualmente (mantenimiento) — opcional. */
    void bloquearFechas(String alojamientoId, LocalDate desde, LocalDate hasta, String motivo);
}