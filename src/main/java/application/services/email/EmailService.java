package application.services.email;

import application.dto.email.EmailDTO;
import application.model.Reserva;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void sendMail(EmailDTO emailDTO) throws Exception;

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

    void enviarCodigoRecuperacion(String email, String codigo);

    boolean validarEmail(String email);

    void enviarReservaAprobada(Reserva reservaActualizada);

    void enviarReservaRechazada(Reserva reservaActualizada);

    void sendMail(String destinatarioEmail, String asunto, String contenido);
}