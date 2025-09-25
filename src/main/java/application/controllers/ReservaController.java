package application.controllers;

import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.dto.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CrearReservaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "Reserva creada"));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ResponseDTO<String>> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "Reserva cancelada"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservaDTO>> get(@PathVariable String id) {
        return ResponseEntity.ok(new ResponseDTO<>(false, null));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> listAll(
            @RequestParam(required = false) String usuarioId,
            @RequestParam(required = false) String estado
    ) {
        List<ReservaDTO> list = new ArrayList<>();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }
}
