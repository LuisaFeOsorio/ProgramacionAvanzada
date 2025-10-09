package application.services.impl;

import application.dto.email.EmailDTO;
import application.model.Reserva;
import application.model.Usuario;
import application.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final Mailer mailer;

    private static final String REMITENTE_NOMBRE = "Plataforma de Reservas";
    private static final String REMITENTE_EMAIL = "noreply@miplataforma.com";

    @Override
    public void enviarConfirmacionReserva(Reserva reserva) {
        String asunto = "✅ Confirmación de Reserva - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoConfirmacionReserva(reserva);

        enviarEmail(
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombre(),
                asunto,
                contenido
        );
    }

    @Override
    public void enviarNotificacionNuevaReservaAnfitrion(Reserva reserva) {
        Usuario anfitrion = reserva.getAlojamiento().getAnfitrion();
        String asunto = "📅 Nueva Reserva Recibida - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoNuevaReservaAnfitrion(reserva);

        enviarEmail(
                anfitrion.getEmail(),
                anfitrion.getNombre(),
                asunto,
                contenido
        );
    }

    @Override
    public void enviarNotificacionCancelacionUsuario(Reserva reserva) {
        String asunto = "❌ Reserva Cancelada - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoCancelacionUsuario(reserva);

        enviarEmail(
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombre(),
                asunto,
                contenido
        );
    }

    public void sendMail(String destinatarioEmail, String asunto, String contenido) {
        EmailDTO emailDTO = new EmailDTO(destinatarioEmail, asunto, contenido);
        enviarEmail(emailDTO.recipient(), emailDTO.subject(), emailDTO.body());
    }


    @Override
    public void enviarNotificacionCancelacionAnfitrion(Reserva reserva) {
        Usuario anfitrion = reserva.getAlojamiento().getAnfitrion();
        String asunto = "❌ Reserva Cancelada - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoCancelacionAnfitrion(reserva);

        enviarEmail(
                anfitrion.getEmail(),
                anfitrion.getNombre(),
                asunto,
                contenido
        );
    }

    @Override
    public void enviarNotificacionAprobacionReserva(Reserva reserva) {
        String asunto = "✅ Reserva Aprobada - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoAprobacionReserva(reserva);

        enviarEmail(
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombre(),
                asunto,
                contenido
        );
    }

    @Override
    public void enviarNotificacionRechazoReserva(Reserva reserva) {
        String asunto = "❌ Reserva Rechazada - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoRechazoReserva(reserva);

        enviarEmail(
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombre(),
                asunto,
                contenido
        );
    }

    @Override
    public void enviarRecordatorioCheckIn(Reserva reserva) {
        String asunto = "🔔 Recordatorio Check-In - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoRecordatorioCheckIn(reserva);

        enviarEmail(
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombre(),
                asunto,
                contenido
        );
    }

    @Override
    public void enviarConfirmacionReservaUsuario(Reserva reserva) {
        enviarConfirmacionReserva(reserva); // Reutilizar
    }

    @Override
    public void enviarSolicitudComentario(Reserva reserva) {
        String asunto = "💬 Cuéntanos tu experiencia - " + reserva.getAlojamiento().getNombre();
        String contenido = construirContenidoSolicitudComentario(reserva);

        enviarEmail(
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getNombre(),
                asunto,
                contenido
        );
    }

    // ✅ EMAIL GENÉRICO CON SIMPLE JAVA MAIL
    @Override
    public void enviarEmail(String destinatario, String asunto, String contenido) {
        enviarEmail(destinatario, null, asunto, contenido);
    }

    // ✅ MÉTODO PRINCIPAL DE ENVÍO
    private void enviarEmail(String destinatarioEmail, String destinatarioNombre, String asunto, String contenido) {
        try {
            var email = EmailBuilder.startingBlank()
                    .from(REMITENTE_NOMBRE, REMITENTE_EMAIL)
                    .to(destinatarioNombre != null ? destinatarioNombre : "Usuario", destinatarioEmail)
                    .withSubject(asunto)
                    .withPlainText(contenido)
                    .buildEmail();

            mailer.sendMail(email);
            System.out.println("📧 Email enviado a: " + destinatarioEmail);

        } catch (Exception e) {
            System.err.println("❌ Error enviando email a " + destinatarioEmail + ": " + e.getMessage());
            // No relanzar la excepción para no romper el flujo principal
        }
    }

    @Override
    public boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Simple Java Mail tiene validación integrada, pero hacemos una básica
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    @Override
    public void enviarReservaAprobada(Reserva reserva) {
        enviarNotificacionAprobacionReserva(reserva);
    }

    @Override
    public void enviarReservaRechazada(Reserva reserva) {
        enviarNotificacionRechazoReserva(reserva);
    }

    // 🔧 MÉTODOS PARA CONSTRUIR CONTENIDO (LOS MISMOS QUE ANTES)

    private String construirContenidoConfirmacionReserva(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        ¡Tu reserva ha sido confirmada! 🎉
                        
                        📋 DETALLES DE TU RESERVA:
                        • Alojamiento: %s
                        • Dirección: %s
                        • Check-in: %s
                        • Check-out: %s
                        • Huéspedes: %d
                        • Estado: CONFIRMADA
                        
                        💡 Información importante:
                        - Presenta este correo al check-in
                        - El check-in es a partir de las 15:00
                        - El check-out es antes de las 11:00
                        
                        📞 Contacto del anfitrión:
                        • Nombre: %s
                        • Email: %s
                        • Teléfono: %s
                        
                        ¡Esperamos que tengas una estancia maravillosa!
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getAlojamiento().getDireccion(),
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes(),
                reserva.getAlojamiento().getAnfitrion().getNombre(),
                reserva.getAlojamiento().getAnfitrion().getEmail(),
                reserva.getAlojamiento().getAnfitrion().getTelefono() != null ?
                        reserva.getAlojamiento().getAnfitrion().getTelefono() : "No disponible"
        );
    }

    private String construirContenidoNuevaReservaAnfitrion(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        ¡Tienes una nueva reserva! 🎊
                        
                        📋 DETALLES DE LA RESERVA:
                        • Alojamiento: %s
                        • Huésped: %s
                        • Email: %s
                        • Teléfono: %s
                        • Check-in: %s
                        • Check-out: %s
                        • Huéspedes: %d
                        • Estado: PENDIENTE
                        
                        ⚡ Acción requerida:
                        Por favor, confirma o rechaza esta reserva en tu panel de anfitrión.
                        
                        ¡Gracias por ser parte de nuestra comunidad!
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getAlojamiento().getAnfitrion().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getTelefono() != null ?
                        reserva.getUsuario().getTelefono() : "No disponible",
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes()
        );
    }

    private String construirContenidoCancelacionUsuario(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        Tu reserva ha sido cancelada.
                        
                        📋 RESERVA CANCELADA:
                        • Alojamiento: %s
                        • Fechas: %s a %s
                        • Huéspedes: %d
                        
                        💰 Reembolso:
                        - Si aplica reembolso, se procesará en 5-7 días hábiles
                        - El monto se acreditará en tu método de pago original
                        
                        😔 Lamentamos cualquier inconveniente
                        Esperamos verte de nuevo pronto.
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes()
        );
    }

    private String construirContenidoCancelacionAnfitrion(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        Una reserva ha sido cancelada.
                        
                        📋 RESERVA CANCELADA:
                        • Alojamiento: %s
                        • Huésped: %s
                        • Fechas: %s a %s
                        • Huéspedes: %d
                        
                        📊 Impacto en tus estadísticas:
                        - Esta cancelación afectará tu tasa de finalización
                        - Las cancelaciones frecuentes pueden afectar tu visibilidad
                        
                        💡 Recomendación:
                        - Considera ajustar tu política de cancelación
                        - Mantén tu calendario actualizado
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getAlojamiento().getAnfitrion().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getUsuario().getNombre(),
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes()
        );
    }

    private String construirContenidoAprobacionReserva(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        ¡Buena noticia! Tu reserva ha sido aprobada por el anfitrión. ✅
                        
                        📋 DETALLES APROBADOS:
                        • Alojamiento: %s
                        • Dirección: %s
                        • Check-in: %s
                        • Check-out: %s
                        • Huéspedes: %d
                        
                        🗓️ Próximos pasos:
                        1. Prepárate para tu viaje
                        2. Contacta al anfitrión si necesitas información adicional
                        3. Presenta identificación al check-in
                        
                        📞 Contacto del anfitrión:
                        • %s
                        • %s
                        
                        ¡Que tengas un excelente viaje!
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getAlojamiento().getDireccion(),
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes(),
                reserva.getAlojamiento().getAnfitrion().getNombre(),
                reserva.getAlojamiento().getAnfitrion().getEmail()
        );
    }

    private String construirContenidoRechazoReserva(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        Lamentamos informarte que tu reserva ha sido rechazada.
                        
                        📋 RESERVA RECHAZADA:
                        • Alojamiento: %s
                        • Fechas: %s a %s
                        
                        🔍 Posibles razones:
                        - El alojamiento no está disponible en esas fechas
                        - El anfitrión no puede aceptar la reserva
                        - Capacidad no disponible
                        
                        💡 Alternativas:
                        - Busca otros alojamientos similares
                        - Ajusta tus fechas de viaje
                        - Contacta al servicio al cliente si necesitas ayuda
                        
                        Lamentamos los inconvenientes y esperamos poder ayudarte a encontrar el alojamiento perfecto.
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getCheckIn(),
                reserva.getCheckOut()
        );
    }

    private String construirContenidoRecordatorioCheckIn(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        ¡Tu check-in está cerca! 🎉
                        
                        📅 Recordatorio de reserva:
                        • Alojamiento: %s
                        • Check-in: %s (mañana)
                        • Check-out: %s
                        • Dirección: %s
                        
                        🎒 Prepárate para tu viaje:
                        - Revisa las instrucciones de check-in
                        - Ten a mano tu identificación
                        - Contacta al anfitrión si llegas fuera del horario establecido
                        
                        📞 Contacto del anfitrión:
                        • %s - %s
                        
                        ¡Que tengas un viaje seguro y una estancia maravillosa!
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getAlojamiento().getDireccion(),
                reserva.getAlojamiento().getAnfitrion().getNombre(),
                reserva.getAlojamiento().getAnfitrion().getEmail()
        );
    }

    private String construirContenidoSolicitudComentario(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        Esperamos que hayas tenido una estancia agradable en %s.
                        
                        Tu opinión es muy importante para nosotros y para la comunidad de viajeros.
                        
                        🌟 Por favor, comparte tu experiencia:
                        - Califica el alojamiento
                        - Escribe un comentario sobre tu estancia
                        - Ayuda a otros viajeros a tomar la mejor decisión
                        
                        ⏱️ Solo te tomará 2 minutos y harás una gran diferencia.
                        
                        ¡Gracias por ser parte de nuestra comunidad!
                        
                        Saludos cordiales,
                        El equipo de la plataforma
                        
                        PD: Las reseñas honestas ayudan a mantener la calidad de nuestra plataforma.
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre()
        );
    }
}
