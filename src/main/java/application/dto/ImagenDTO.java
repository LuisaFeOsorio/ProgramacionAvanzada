package application.dto;

public record ImagenDTO(
        String id,
        String url,
        String descripcion,
        boolean principal
) {
}
