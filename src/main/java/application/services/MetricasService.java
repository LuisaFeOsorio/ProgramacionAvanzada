package application.services;

import application.dto.MetricasAlojamientoDTO;

import java.time.LocalDate;
import java.util.List;

public interface MetricasService {
    /** Devuelve métricas básicas: total reservas y calificación promedio (filtrable por rango). */
    MetricasAlojamientoDTO obtenerMetricas(String alojamientoId, LocalDate desde, LocalDate hasta);

    /** Métricas agregadas para un anfitrión: sumarios por alojamiento. */
    List<MetricasAlojamientoDTO> obtenerMetricasAnfitrion(String anfitrionId, LocalDate desde, LocalDate hasta);
}
