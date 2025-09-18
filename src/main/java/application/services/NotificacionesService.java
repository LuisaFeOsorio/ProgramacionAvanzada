package application.services;

public interface NotificacionesService {
    /** Enviar email (plantilla) */
    void enviarEmail(String to, String subject, String body);

    /** Enviar notificación en-app a usuario (se puede hacer persistente). */
    void enviarNotificacionUsuario(String userId, String titulo, String mensaje);

    /** Enviar notificación push/SMS (si aplica) */
    void enviarPush(String userId, String message);
}
