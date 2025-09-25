package application.dto.imagen;

public record ImagenDTO(
        String id,
        String url,
        String descripcion,
        boolean principal
) {
}
