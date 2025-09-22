package application.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {


    @PostMapping
    public String upload() {
        return "Endpoint para subir imagen (sin implementar)";
    }

    @GetMapping("/{id}")
    public String get(@PathVariable String id) {
        return "Endpoint para obtener imagen con id: " + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        return "Endpoint para eliminar imagen con id: " + id;
    }
}
