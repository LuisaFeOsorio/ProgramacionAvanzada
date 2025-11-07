package application.mappers;

import application.dto.comentario.ComentarioDTO;
import application.dto.comentario.ActualizarComentarioDTO;
import application.model.Comentario;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    // ✅ MAPEO A DTO SIMPLIFICADO
    @Mapping(target = "usuarioNombre", source = "usuario.nombre")
    @Mapping(target = "usuarioFoto", source = "usuario.foto_perfil")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "alojamientoNombre", source = "alojamiento.nombre")
    @Mapping(target = "alojamientoId", source = "alojamiento.id")
    @Mapping(target = "fechaEstadia", source = "reserva.checkIn")

    ComentarioDTO toDTO(Comentario comentario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "reserva", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "respuesta", ignore = true)
    void updateFromDTO(ActualizarComentarioDTO dto, @MappingTarget Comentario comentario);
}