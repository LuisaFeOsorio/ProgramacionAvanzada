package application.dto.paginacion;

import application.validators.PaginacionValidator;
import jakarta.validation.constraints.*;
import java.util.List;

public record PaginacionDTO<T>(
        @NotNull
        List<T> contenido,

        @Min(0) int paginaActual,

        @Min(1) @Max(100) int tamanioPagina,

        @Min(0) long totalElementos,

        @Min(0) int totalPaginas,

        boolean primera,

        boolean ultima,

        boolean tieneSiguiente,

        boolean tieneAnterior
) {

    // CONSTRUCTOR PRINCIPAL (sin cálculos booleanos)
    public PaginacionDTO(List<T> contenido, int paginaActual, int tamanioPagina,
                         long totalElementos, int totalPaginas) {
        this(contenido, paginaActual, tamanioPagina, totalElementos, totalPaginas,
                PaginacionValidator.calcularEsPrimera(paginaActual),
                PaginacionValidator.calcularEsUltima(paginaActual, totalPaginas),
                PaginacionValidator.calcularTieneSiguiente(paginaActual, totalPaginas),
                PaginacionValidator.calcularTieneAnterior(paginaActual));
    }

    // ✅ CONSTRUCTOR DESDE PAGE DE SPRING
    public PaginacionDTO(List<T> contenido, org.springframework.data.domain.Page<?> page) {
        this(contenido,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious());
    }
}