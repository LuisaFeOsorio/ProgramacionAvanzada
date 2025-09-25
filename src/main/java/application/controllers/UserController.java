package application.controllers;

import application.dto.usuario.CrearUsuarioDTO;
import application.dto.usuario.EditarUsuarioDTO;
import application.dto.ResponseDTO;
import application.dto.usuario.UsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CrearUsuarioDTO userDTO) throws Exception{
        //Lógica para crear el usuario
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditarUsuarioDTO userDTO) throws Exception{
        //Lógica para editar el usuario
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception{
        //Lógica para eliminar el usuario
        return ResponseEntity.ok(new ResponseDTO<>(false, "El usuario ha sido eliminado"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> get(@PathVariable String id) throws Exception{
        return ResponseEntity.ok(new ResponseDTO<>(false, null));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UsuarioDTO>>> listAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone
    ) {
        //Lógica para consultar todos los usuarios con filtros
        List<UsuarioDTO> list = new ArrayList<>();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }
}
