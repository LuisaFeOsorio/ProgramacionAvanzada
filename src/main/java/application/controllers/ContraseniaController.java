package application.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class ContraseniaController {

    // DTO con la info para resetear contraseña
    public static class ResetContraseniaDTO {
        private String email;
        private String nuevaContrasenia;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getNuevaContrasenia() { return nuevaContrasenia; }
        public void setNuevaContrasenia(String nuevaContrasenia) { this.nuevaContrasenia = nuevaContrasenia; }
    }

    @PostMapping("/reset")
    public String reset(@RequestBody ResetContraseniaDTO dto) {
        // Aquí la lógica para resetear la contraseña
        return "Contraseña reseteada para: " + dto.getEmail();
    }

    @PostMapping("/change")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        // Aquí la lógica para cambiar contraseña con la antigua
        return "Contraseña cambiada correctamente";
    }
}
