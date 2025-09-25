package application.controllers;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CrearAlojamientoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "Alojamiento creado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditarAlojamientoDTO dto) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "Alojamiento eliminado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<AlojamientoDTO>> get(@PathVariable String id) {
        return ResponseEntity.ok(new ResponseDTO<>(false, null));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AlojamientoDTO>>> listAll(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) Double precioMax
    ) {
        List<AlojamientoDTO> list = new ArrayList<>();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }
}
