package application.services;

import java.io.InputStream;
import java.util.List;

public interface ImagenService {
    String subirImagen(String alojamientoId, InputStream imageStream, String filename, String contentType);

    /** Elimina imagen (y actualiza alojamiento si era principal). */
    void eliminarImagen(String alojamientoId, String imageId);

    /** Lista URLs/IDs de las imágenes de un alojamiento. */
    List<String> listarImagenes(String alojamientoId);

    /** Validaciones: máximo 10 imágenes, mínimo 1. */
    boolean puedeAgregarImagen(String alojamientoId);
}
