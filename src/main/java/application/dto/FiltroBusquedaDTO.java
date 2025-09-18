package application.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record FiltroBusquedaDTO(

        @Size(max = 100)
        String ciudad,

        // Rango de fechas (opcional)
        LocalDate fechaInicio,
        LocalDate fechaFin,

        // Rango de precio por noche (opcional)
        @PositiveOrZero
        Double precioMin,
        @PositiveOrZero
        Double precioMax,

        // Lista de servicios (ej. "wifi","piscina") -- elementos no vacíos
        List<@NotBlank String> servicios,

        // Paginación (por defecto la implementación puede usar 1 y 10)
        @Min(1)
        Integer pagina,
        @Min(1)
        Integer size,

        // Opcional: búsqueda por ubicación geográfica (mapa)
        Double lat,
        Double lng,
        @Positive
        Double radioKm, // en kilómetros

        // Filtros adicionales
        @Min(1)
        Integer capacidad, // número de huéspedes

        @Size(max = 50)
        String ordenar){} //