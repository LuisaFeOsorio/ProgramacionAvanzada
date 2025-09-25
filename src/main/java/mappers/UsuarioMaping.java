package mappers;

import application.dto.usuario.CrearUsuarioDTO;
import application.dto.usuario.EditarUsuarioDTO;
import application.dto.usuario.UsuarioDTO;
import application.model.entidades.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMaping {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "role", constant = "GUEST")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Usuario toEntity(CrearUsuarioDTO userDTO);

    UsuarioDTO toUserDTO(Usuario user);

    void updateUserFromDto(EditarUsuarioDTO dto, @MappingTarget Usuario user);
}
