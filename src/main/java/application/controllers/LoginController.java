package application.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Aquí va la lógica de login (validar usuario y contraseña, generar token, etc.)
        return "Usuario autenticado correctamente: " + username;
    }
}
