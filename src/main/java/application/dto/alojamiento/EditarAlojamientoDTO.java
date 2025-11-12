package application.dto.alojamiento;

import application.model.enums.TipoAlojamiento;

import java.util.List;

public record EditarAlojamientoDTO(
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
        List<String> imagenes
) {}