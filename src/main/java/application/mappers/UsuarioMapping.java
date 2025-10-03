package application.mappers;

import application.dto.usuario.CrearUsuarioDTO;
import application.dto.usuario.EditarUsuarioDTO;
import application.dto.usuario.UsuarioDTO;
import application.model.entidades.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapping {

    // Crear un Usuario desde CrearUsuarioDTO
    @Mapping(source = "fotoPerfil", target = "fotoPerfil")
    @Mapping(source = "fechaNacimiento", target = "fechaNacimiento")
    @Mapping(source = "rol", target = "rol")
    @Mapping(target = "alojamientos", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "id", ignore = true)
    Usuario toEntity(CrearUsuarioDTO dto);

    // Convertir un Usuario a UsuarioDTO
    @Mapping(source = "fotoPerfil", target = "fotoPerfil")
    @Mapping(source = "fechaNacimiento", target = "fechaNacimiento")
    @Mapping(source = "rol", target = "rol")
    UsuarioDTO toUserDTO(Usuario user);

    // Actualizar Usuario desde EditarUsuarioDTO
    @Mapping(target = "id", ignore = true) // no se toca el id
    @Mapping(target = "email", ignore = true) // el email no se edita aquí
    @Mapping(target = "activo", ignore = true) // control aparte
    @Mapping(target = "alojamientos", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(source = "fotoPerfil", target = "fotoPerfil")
    @Mapping(source = "rol", target = "rol")
    void updateUserFromDto(EditarUsuarioDTO dto, @MappingTarget Usuario user);
}
