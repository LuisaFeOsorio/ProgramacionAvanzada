package application.dto.email;

public record EmailDTO(
        String recipient,  // destinatario
        String subject,    // asunto (corto)
        String body
) {
}