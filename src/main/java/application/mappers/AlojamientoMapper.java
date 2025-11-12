package application.mappers;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.model.Alojamiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlojamientoMapper {

    @Mapping(target = "capacidadMaxima", source = "capacidadMaxima")
    @Mapping(target = "numeroHabitaciones", source = "numeroHabitaciones")
    @Mapping(target = "numeroBanos", source = "numeroBanos")
    @Mapping(target = "imagenes", source = "imagenes")
    @Mapping(target = "imagenPrincipal", expression = "java(obtenerImagenPrincipal(alojamiento))")
    // Nuevos mapeos
    @Mapping(target = "anfitrionId", source = "anfitrion.id")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "totalCalificaciones", expression = "java(calcularTotalCalificaciones(alojamiento))")
    AlojamientoDTO toDTO(Alojamiento alojamiento);

    List<AlojamientoDTO> toDTOList(List<Alojamiento> alojamientos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "anfitrion", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "calificacionPromedio", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "totalCalificaciones", ignore = true)
    void updateFromEditarDTO(EditarAlojamientoDTO dto, @MappingTarget Alojamiento alojamiento);

    default String obtenerImagenPrincipal(Alojamiento alojamiento) {
        if (alojamiento.getImagenes() == null || alojamiento.getImagenes().isEmpty()) {
            return null;
        }
        return alojamiento.getImagenes().get(0);
    }

    default Integer calcularTotalCalificaciones(Alojamiento alojamiento) {
        if (alojamiento.getComentarios() == null) {
            return 0;
        }
        return (int) alojamiento.getComentarios().stream()
                .filter(comentario -> comentario.getCalificacion() != null)
                .count();
    }
}