package application.dto;

public record ResponseDTO<T>(
        boolean error,
        T content
) {
}
