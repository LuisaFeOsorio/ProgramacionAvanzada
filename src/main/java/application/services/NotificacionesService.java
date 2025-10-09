package application.services;

public interface NotificacionesService {

    void enviarEmail(String to, String subject, String body);

    void crearNotificacionUsuario(Long usuarioId, String titulo, String mensaje);

    void enviarNotificacionUsuario(String userId, String titulo, String mensaje);

    void enviarPush(String userId, String message);

    void crearNotificacionAnfitrion(Long anfitrionId, String titulo, String mensaje);

}
