package application.dto.email;

public record EmailDTO(
        String recipient,
        String body,
        String subject
) {
}