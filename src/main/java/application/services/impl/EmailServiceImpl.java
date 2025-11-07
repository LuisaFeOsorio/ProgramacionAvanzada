package application.services.impl;

import application.dto.email.EmailDTO;
import application.model.Reserva;
import application.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${smtp.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${smtp.port:587}")
    private int smtpPort;

    @Value("${smtp.username:pruebasprogramaciondl@gmail.com}")
    private String smtpUsername;

    @Value("${smtp.password:lrap tzji ctyf swvh}") // Tu contraseña de aplicación de 16 caracteres SIN espacios
    private String smtpPassword;

    @Value("${smtp.from.name:Plataforma de Reservas}")
    private String smtpFromName;

    @Async

    @Override
    public void sendMail(EmailDTO emailDTO) throws Exception {
        System.out.println("📧 Preparando envío a: " + emailDTO.recipient());
        System.out.println("📝 Asunto: " + emailDTO.subject());
        System.out.println("📄 Contenido (primeros 50 chars): " +
                (emailDTO.body() != null ? emailDTO.body().substring(0, Math.min(50, emailDTO.body().length())) : "null"));

        var email = EmailBuilder.startingBlank()
                .from(smtpFromName, smtpUsername)
                .to(emailDTO.recipient())
                .withSubject(emailDTO.subject()) // Solo el título aquí
                .withPlainText(emailDTO.body())   // El contenido aquí
                .buildEmail();

        try {
            Mailer mailer = MailerBuilder
                    .withSMTPServer(smtpHost, smtpPort, smtpUsername, smtpPassword)
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .withSessionTimeout(10 * 1000)
                    .withDebugLogging(true)
                    .buildMailer();

            mailer.sendMail(email);
            System.out.println("✅ Email enviado exitosamente a: " + emailDTO.recipient());

        } catch (Exception e) {
            System.err.println("❌ Error enviando email a " + emailDTO.recipient() + ": " + e.getMessage());
            throw new Exception("Error enviando email: " + e.getMessage(), e);
        }
    }

    @Override
    public void enviarCodigoRecuperacion(String email, String codigo) {
        // ✅ CORRECTO: Asunto corto y específico
        String asunto = "🔐 Código de Recuperación - Plataforma de Reservas";

        // ✅ CORRECTO: Contenido en el body
        String contenido = construirContenidoCodigoRecuperacion(codigo);

        EmailDTO emailDTO = new EmailDTO(email, asunto, contenido);

        try {
            sendMail(emailDTO);
            System.out.println("📧 Código de recuperación enviado a: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error enviando código a " + email + ": " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el código de recuperación", e);
        }
    }

    @Override
    public void enviarConfirmacionReserva(application.model.Reserva reserva) {
        String asunto = "✅ Confirmación de Reserva - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoConfirmacionReserva(reserva);

        EmailDTO emailDTO = new EmailDTO(
                reserva.getUsuario().getEmail(),
                asunto,
                contenido
        );

        try {
            sendMail(emailDTO);
        } catch (Exception e) {
            System.err.println("❌ Error enviando confirmación: " + e.getMessage());
        }
    }

    private String construirContenidoConfirmacionReserva(Reserva reserva) {
        return "";
    }

    @Override
    public void enviarNotificacionNuevaReservaAnfitrion(application.model.Reserva reserva) {
        String asunto = "📅 Nueva Reserva Recibida - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoNuevaReservaAnfitrion(reserva);

        EmailDTO emailDTO = new EmailDTO(
                reserva.getAlojamiento().getAnfitrion().getEmail(),
                asunto,
                contenido
        );

        try {
            sendMail(emailDTO);
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación anfitrión: " + e.getMessage());
        }
    }

    private String construirContenidoNuevaReservaAnfitrion(Reserva reserva) {
        return "";
    }

    @Override
    public void enviarEmail(String destinatario, String asunto, String contenido) {
        EmailDTO emailDTO = new EmailDTO(destinatario, asunto, contenido);
        try {
            sendMail(emailDTO);
        } catch (Exception e) {
            System.err.println("❌ Error enviando email genérico: " + e.getMessage());
        }
    }

    // ... ADAPTA TODOS TUS OTROS MÉTODOS de la misma forma

    public String construirContenidoCodigoRecuperacion(String codigo) {
        return """
            Hola,
            
            Has solicitado restablecer tu contraseña en nuestra plataforma.
            
            🔒 Tu código de verificación es: **%s**
            
            ⏰ Este código expirará en 10 minutos.
            
            Si no solicitaste este cambio, por favor ignora este mensaje.
            
            Saludos,
            Equipo de Plataforma de Reservas
            """.formatted(codigo);
    }

    // ... MANTÉN TODOS TUS MÉTODOS de construcción de contenido existentes
    // construirContenidoConfirmacionReserva, construirContenidoNuevaReservaAnfitrion, etc.

    @Override
    public boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    @Override
    public void enviarReservaAprobada(Reserva reservaActualizada) {

    }

    @Override
    public void enviarReservaRechazada(Reserva reservaActualizada) {

    }

    @Override
    public void sendMail(String destinatarioEmail, String asunto, String contenido) {

    }

    // ... IMPLEMENTA LOS DEMÁS MÉTODOS adaptándolos a usar sendMail
    @Override
    public void enviarNotificacionCancelacionUsuario(application.model.Reserva reserva) {
        String asunto = "❌ Reserva Cancelada - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoCancelacionUsuario(reserva);

        EmailDTO emailDTO = new EmailDTO(reserva.getUsuario().getEmail(), asunto, contenido);
        try { sendMail(emailDTO); } catch (Exception e) { /* manejar error */ }
    }

    private String construirContenidoCancelacionUsuario(Reserva reserva) {
        return "";
    }

    @Override
    public void enviarNotificacionCancelacionAnfitrion(application.model.Reserva reserva) {
        String asunto = "❌ Reserva Cancelada - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoCancelacionAnfitrion(reserva);

        EmailDTO emailDTO = new EmailDTO(reserva.getAlojamiento().getAnfitrion().getEmail(), asunto, contenido);
        try { sendMail(emailDTO); } catch (Exception e) { /* manejar error */ }
    }

    private String construirContenidoCancelacionAnfitrion(Reserva reserva) {
        return "";
    }

    @Override
    public void enviarNotificacionAprobacionReserva(Reserva reserva) {

    }

    @Override
    public void enviarNotificacionRechazoReserva(Reserva reserva) {

    }

    @Override
    public void enviarRecordatorioCheckIn(Reserva reserva) {

    }

    @Override
    public void enviarConfirmacionReservaUsuario(Reserva reserva) {

    }

    @Override
    public void enviarSolicitudComentario(Reserva reserva) {

    }

    // ... continúa con los demás métodos
}