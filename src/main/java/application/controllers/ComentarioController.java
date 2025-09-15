package application.controllers;

import application.dto.ComentarioDTO;
import application.dto.CrearComentarioDTO;
import application.dto.ResponseDTO;
import application.dto.RespuestaComentarioDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CrearComentarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "Comentario creado"));
    }

    @PostMapping("/{id}/respuesta")
    public ResponseEntity<ResponseDTO<String>> responder(@PathVariable String id, @Valid @RequestBody RespuestaComentarioDTO dto) {
        return ResponseEntity.ok(new ResponseDTO<>(false, "Respuesta agregada al comentario"));
    }

    @GetMapping("/alojamiento/{idAlojamiento}")
    public ResponseEntity<ResponseDTO<List<ComentarioDTO>>> listByAlojamiento(@PathVariable String idAlojamiento) {
        List<ComentarioDTO> list = new ArrayList<>();
        return ResponseEntity.ok(new ResponseDTO<>(false, list));
    }
}
