package application.services;

import java.util.List;

import application.dto.CrearUsuarioDTO;
import application.dto.EditarUsuarioDTO;
import application.dto.UserDTO;


public interface UserService {

    void create( CrearUsuarioDTO usuarioDTO) throws Exception;

    UserDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<UserDTO> listAll();

    void edit(String id, EditarUsuarioDTO userDTO) throws Exception;

}
