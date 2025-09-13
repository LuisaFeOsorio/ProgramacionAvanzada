package application.controllers;

import application.dto.CreateUserDTO;
import application.dto.EditUserDTO;
import application.dto.UserDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public void create(CreateUserDTO userDTO) throws Exception{
    }

    public void edit(EditUserDTO userDTO) throws Exception{
    }

    public void delete(String id) throws Exception{
    }

    public UserDTO get(String id) throws Exception{
        return null;
    }

    public List<UserDTO> listAll(){
        return List.of();
    }
}
