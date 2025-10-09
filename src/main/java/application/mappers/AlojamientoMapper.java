package application.mappers;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.model.Alojamiento;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlojamientoMapper {

    // ✅ MAPEO A DTO PRINCIPAL
    @Mapping(target = "capacidad", source = "capacidadMaxima")
    AlojamientoDTO toDTO(Alojamiento alojamiento);

    // ✅ MAPEO PARA LISTA (ahora funciona sin ambigüedad)
    List<AlojamientoDTO> toDTOList(List<Alojamiento> alojamientos);

    // ✅ ACTUALIZACIÓN PARCIAL
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "anfitrion", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "pais", ignore = true)
    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "calificacionPromedio", ignore = true)
    @Mapping(target = "totalCalificaciones", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    @Mapping(target = "servicios", ignore = true)
    @Mapping(target = "capacidadMaxima", ignore = true)
    void updateFromEditarDTO(EditarAlojamientoDTO dto, @MappingTarget Alojamiento alojamiento);

    // ❌ ELIMINADO: toDTOSimple - usa toDTO para todo
}