package application.services;

import java.util.List;

import application.dto.usuario.CrearUsuarioDTO;
import application.dto.usuario.EditarUsuarioDTO;
import application.dto.usuario.UsuarioDTO;


public interface UserService {

    void create( CrearUsuarioDTO usuarioDTO) throws Exception;

    UsuarioDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<UsuarioDTO> listAll();

    void edit(String id, EditarUsuarioDTO userDTO) throws Exception;

}
