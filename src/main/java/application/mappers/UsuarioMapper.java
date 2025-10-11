package application.mappers;

import application.dto.usuario.CrearUsuarioDTO;
import application.dto.usuario.EditarUsuarioDTO;
import application.dto.usuario.UsuarioDTO;
import application.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "descripcionPersonal", ignore = true)
    @Mapping(target = "documentoIdentidad", ignore = true)
    @Mapping(target = "archivoDocumentos", ignore = true)
    @Mapping(target = "documentosVerificados", ignore = true)
    @Mapping(target = "alojamientos", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    Usuario toEntity(CrearUsuarioDTO crearUsuarioDTO);

    @Mapping(source = "rol", target = "rol")
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true) // El email no se debería poder editar fácilmente
    @Mapping(target = "contrasenia", ignore = true) // La contraseña se cambia con endpoint específico
    @Mapping(target = "rol", ignore = true) // El rol se cambia con endpoints específicos
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "descripcionPersonal", ignore = true)
    @Mapping(target = "documentoIdentidad", ignore = true)
    @Mapping(target = "archivoDocumentos", ignore = true)
    @Mapping(target = "documentosVerificados", ignore = true)
    @Mapping(target = "alojamientos", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    void updateEntityFromDTO(EditarUsuarioDTO editarUsuarioDTO, @org.mapstruct.MappingTarget Usuario usuario);
}