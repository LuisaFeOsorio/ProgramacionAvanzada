package application.controllers;

import application.dto.ResponseDTO;
import application.services.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor

public class ImagenController {

    private final ImagenService imageService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<Map>> upload(@RequestParam("file") MultipartFile image) throws Exception{
        Map response = imageService.upload(image);
        return ResponseEntity.ok( new ResponseDTO<>(false, response) );
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete(@RequestParam("id") String id) throws Exception{
        imageService.delete(id);
        return ResponseEntity.ok( new ResponseDTO<>(false, "Imagen eliminada exitosamente") );
    }

}