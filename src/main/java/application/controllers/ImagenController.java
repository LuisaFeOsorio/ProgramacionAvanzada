package application.controllers;

import application.dto.ResponseDTO;
import application.services.imagen.ImagenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imageService;

    // ✅ UPLOAD CON VALIDACIONES MEJORADAS
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> upload(
            @RequestParam("file") @Valid MultipartFile image) {

        try {
            // Validar tipo de archivo
            if (image.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO<>(true, "El archivo no puede estar vacío", null));
            }

            // Validar tipo MIME
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO<>(true, "Solo se permiten archivos de imagen", null));
            }

            // Validar tamaño (ej: máximo 5MB)
            if (image.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO<>(true, "La imagen no puede exceder 5MB", null));
            }

            Map<String, Object> response = imageService.upload(image);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Imagen subida exitosamente", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al subir la imagen: " + e.getMessage(), null));
        }
    }

    // ✅ DELETE MEJORADO
    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete(
            @RequestParam("id") @NotBlank String imageId) {

        try {
            imageService.delete(imageId);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Imagen eliminada exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al eliminar la imagen: " + e.getMessage(), null));
        }
    }

    // ✅ NUEVO: UPLOAD MÚLTIPLE
    @PostMapping("/multiple")
    public ResponseEntity<ResponseDTO<List<Map<String, Object>>>> uploadMultiple(
            @RequestParam("files") MultipartFile[] images) {

        try {
            List<Map<String, Object>> responses = new ArrayList<>();

            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    Map<String, Object> response = imageService.upload(image);
                    responses.add(response);
                }
            }

            return ResponseEntity.ok(new ResponseDTO<>(false,
                    responses.size() + " imágenes subidas exitosamente", responses));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(true, "Error al subir imágenes: " + e.getMessage(), null));
        }
    }
}