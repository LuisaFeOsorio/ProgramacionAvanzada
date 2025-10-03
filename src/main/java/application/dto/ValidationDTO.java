package application.dto;


public record ValidationDTO(
        String field,
        String message
) {
}