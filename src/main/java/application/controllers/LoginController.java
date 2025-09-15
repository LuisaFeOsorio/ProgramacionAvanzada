package application.controllers;

import application.dto.LoginDTO;
import application.dto.LoginDTO;
import application.dto.CrearUsuarioDTO;
import application.dto.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginDTO>> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(new ResponseDTO<>(false, new LoginDTO("tokenEjemplo","contrasenia")));
    }

    @PostMapping("/registro")
    public ResponseEntity<ResponseDTO<String>> register(@Valid @RequestBody CrearUsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "El registro ha sido exitoso"));
    }

    @PostMapping("/recuperar")
    public ResponseEntity<ResponseDTO<String>> recuperar(@RequestParam String correo) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "Se ha enviado un enlace de recuperación"));
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseDTO<String>> reset(@RequestParam String token, @RequestParam String nuevaContrasena) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "La contraseña ha sido restablecida"));
    }
}
