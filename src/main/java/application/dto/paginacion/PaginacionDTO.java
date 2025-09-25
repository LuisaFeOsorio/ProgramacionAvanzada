package application.dto.paginacion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;


public record PaginacionDTO<T>(
        @PositiveOrZero
        long total,            // total de elementos (no páginas)

        @Min(1) int paginaActual,      // número de página (1-based)

        @Min(1) int size,              // tamaño de página

        @NotNull List<T> resultados     // lista de resultados en la página
) {}
