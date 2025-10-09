package application.validators;

import application.dto.paginacion.PaginacionDTO;

import java.util.List;

public class PaginacionValidator {

    // ✅ MÉTODOS DE CÁLCULO
    public static boolean calcularEsPrimera(int paginaActual) {
        return paginaActual == 0;
    }

    public static boolean calcularEsUltima(int paginaActual, int totalPaginas) {
        return paginaActual >= totalPaginas - 1;
    }

    public static boolean calcularTieneSiguiente(int paginaActual, int totalPaginas) {
        return paginaActual < totalPaginas - 1;
    }

    public static boolean calcularTieneAnterior(int paginaActual) {
        return paginaActual > 0;
    }

    // ✅ MÉTODO PARA CREAR DESDE PAGE DE SPRING (MÁS SIMPLE)
    public static <T> PaginacionDTO<T> desdePage(List<T> contenido, org.springframework.data.domain.Page<?> page) {
        return new PaginacionDTO<>(
                contenido,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    // ✅ MÉTODOS ADICIONALES ÚTILES
    public static int calcularOffset(int paginaActual, int tamanioPagina) {
        return paginaActual * tamanioPagina;
    }

    public static boolean esPaginaValida(int paginaActual, int totalPaginas) {
        return paginaActual >= 0 && paginaActual < totalPaginas;
    }

    public static int calcularTotalPaginas(long totalElementos, int tamanioPagina) {
        if (totalElementos == 0) return 0;
        return (int) Math.ceil((double) totalElementos / tamanioPagina);
    }
}