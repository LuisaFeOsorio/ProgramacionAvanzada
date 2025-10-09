package application.services.email;

import application.model.Reserva;

public interface EmailService {

    void enviarConfirmacionReserva(Reserva reserva);

    void enviarNotificacionNuevaReservaAnfitrion(Reserva reserva);

    void enviarNotificacionCancelacionUsuario(Reserva reserva);

    void enviarNotificacionCancelacionAnfitrion(Reserva reserva);

    void enviarNotificacionAprobacionReserva(Reserva reserva);

    void enviarNotificacionRechazoReserva(Reserva reserva);

    void enviarRecordatorioCheckIn(Reserva reserva);

    void enviarConfirmacionReservaUsuario(Reserva reserva);

    void enviarSolicitudComentario(Reserva reserva);

    void enviarEmail(String destinatario, String asunto, String contenido);

    boolean validarEmail(String email);

    void enviarReservaAprobada(Reserva reservaActualizada);

    void enviarReservaRechazada(Reserva reservaActualizada);

    void sendMail(String destinatarioEmail, String asunto, String contenido);
}