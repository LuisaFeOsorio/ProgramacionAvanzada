package application.controllers;

import application.dto.CambioContraseniaDTO;
import application.dto.RecuperarContraseniaDTO;
import application.dto.ResetContraseniaDTO;
import application.dto.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ContraseniaController {

    @PutMapping("/cambiar-password")
    public ResponseEntity<ResponseDTO<String>> cambiarPassword(@Valid @RequestBody CambioContraseniaDTO dto) {
        // Lógica para validar la contraseña actual y actualizarla
        return ResponseEntity.ok(new ResponseDTO<>(false, "La contraseña ha sido cambiada exitosamente"));
    }

    @PostMapping("/recuperar")
    public ResponseEntity<ResponseDTO<String>> recuperar(@Valid @RequestBody RecuperarContraseniaDTO dto) {
        // Lógica para generar token y enviar correo
        return ResponseEntity.ok(new ResponseDTO<>(false, "Se ha enviado un enlace de recuperación al correo"));
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseDTO<String>> reset(@Valid @RequestBody ResetContraseniaDTO dto) {
        // Lógica para validar token y asignar nueva contraseña
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "La contraseña ha sido restablecida"));
    }
}
