package application.dto.alojamiento;

import application.model.enums.TipoAlojamiento;

import java.util.List;

public record FiltroBusquedaDTO(
        String ciudad,
        TipoAlojamiento tipo,
        Double precioMin,
        Double precioMax,
        Integer capacidadMin,
        List<String> servicios,
        String query,
        int pagina,
        int tamanio
) {
    // Constructor con valores por defecto
    public FiltroBusquedaDTO {
        if (pagina < 0) pagina = 0;
        if (tamanio <= 0 || tamanio > 100) tamanio = 10;
    }

    // Método estático para crear filtro básico
    public static FiltroBusquedaDTO crear(String ciudad, Integer capacidadMin) {
        return new FiltroBusquedaDTO(ciudad, null, null, null, capacidadMin, null, null, 0, 10);
    }


}