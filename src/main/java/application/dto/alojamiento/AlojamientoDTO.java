package application.dto.alojamiento;

import application.model.enums.TipoAlojamiento;

import java.time.LocalDateTime;
import java.util.List;

public record AlojamientoDTO(
        Long id,
        String nombre,
        String descripcion,
        TipoAlojamiento tipo,
        String ciudad,
        String pais,
        String direccion,
        Double precioPorNoche,
        Integer capacidadMaxima,
        Integer numeroHabitaciones,
        Integer numeroBanos,
        List<String> servicios,
        List<String> imagenes,
        String imagenPrincipal,
        Double calificacionPromedio,
        Boolean activo,
        Long anfitrionId,
        LocalDateTime fechaCreacion,
        Integer totalCalificaciones
) {}