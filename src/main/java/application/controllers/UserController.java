package application.controllers;

import application.dto.CrearUsuarioDTO;
import application.dto.EditarUsuarioDTO;
import application.dto.UsuarioDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public void create(CrearUsuarioDTO userDTO) throws Exception{
    }

    public void edit(EditarUsuarioDTO userDTO) throws Exception{
    }

    public void delete(String id) throws Exception{
    }

    public UsuarioDTO get(String id) throws Exception{
        return null;
    }

    public List<UsuarioDTO> listAll(){
        return List.of();
    }
}
