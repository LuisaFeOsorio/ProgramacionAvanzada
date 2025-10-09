package application.services.impl;

import application.model.Notificacion;
import application.model.Reserva;
import application.model.Usuario;
import application.model.enums.TipoNotificacion;
import application.repositories.NotificacionRepository;
import application.repositories.UsuarioRepository;
import application.services.NotificacionesService;
import application.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionesServiceImpl implements NotificacionesService {

    private final EmailService emailService;
    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    // ✅ ENVIAR EMAIL GENÉRICO
    @Override
    public void enviarEmail(String to, String subject, String body) {
        try {
            log.info("Enviando email a: {}, Asunto: {}", to, subject);
            emailService.enviarEmail(to, subject, body);
            log.info("Email enviado exitosamente a: {}", to);

        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage());
            // No relanzamos la excepción para no romper el flujo principal
        }
    }
    @Override
    public void crearNotificacionUsuario(Long usuarioId, String titulo, String mensaje) {
        try {
            // Verificar que el usuario existe
            Usuario usuario = usuarioRepository.findById((usuarioId))
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

            // Crear notificación en base de datos
            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(usuario);
            notificacion.setTitulo(titulo);
            notificacion.setMensaje(mensaje);
            notificacion.setLeida(false);
            notificacion.setFechaCreacion(LocalDateTime.now());
            notificacion.setTipo(TipoNotificacion.valueOf("USUARIO"));

            notificacionRepository.save(notificacion);
            log.info("Notificación creada para usuario {}: {}", usuarioId, titulo);

            // Enviar notificación push
            enviarPush(usuarioId.toString(), titulo + ": " + mensaje);

        } catch (Exception e) {
            log.error("Error creando notificación para usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al crear notificación: " + e.getMessage(), e);
        }
    }
    // ✅ CREAR NOTIFICACIÓN EN APP PARA USUARIO
    @Override
    public void enviarNotificacionUsuario(String userId, String titulo, String mensaje) {
        try {
            Long usuarioId = Long.valueOf(userId);
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

            if (usuarioOpt.isPresent()) {
                Notificacion notificacion = new Notificacion();
                notificacion.setUsuario(usuarioOpt.get());
                notificacion.setTitulo(titulo);
                notificacion.setMensaje(mensaje);
                notificacion.setTipo(TipoNotificacion.SISTEMA);
                notificacion.setLeida(false);
                notificacion.setFechaCreacion(LocalDateTime.now());

                notificacionRepository.save(notificacion);
                log.info("Notificación creada para usuario {}: {}", userId, titulo);

            } else {
                log.warn("Usuario no encontrado para notificación: {}", userId);
            }

        } catch (Exception e) {
            log.error("Error creando notificación para usuario {}: {}", userId, e.getMessage());
        }
    }
    @Override
    public void crearNotificacionAnfitrion(Long anfitrionId, String titulo, String mensaje) {
        // Verificar que el anfitrión existe
        Usuario anfitrion = usuarioRepository.findById((anfitrionId))
                .orElseThrow(() -> new IllegalArgumentException("Anfitrión no encontrado"));

        // Crear notificación
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(anfitrion);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setTipo(TipoNotificacion.valueOf("ANFITRION"));

        notificacionRepository.save(notificacion);

        // Notificación push opcional
        enviarPush(anfitrionId.toString(), titulo);
    }
    @Override
    public void enviarPush(String userId, String message) {
        try {
            // ⚠️ ESTO ES UNA SIMULACIÓN - En una app real usarías Firebase, OneSignal, etc.
            log.info("📱 [PUSH SIMULADO] Para usuario {}: {}", userId, message);

            // En una implementación real sería algo como:
            // firebaseService.sendPushNotification(userId, message);
            // oneSignalService.sendNotification(userId, message);

        } catch (Exception e) {
            log.error("Error enviando push a usuario {}: {}", userId, e.getMessage());
        }
    }


    private String construirMensajeNuevaReserva(Reserva reserva) {
        return String.format(
                "Tienes una nueva reserva para %s. Huésped: %s, Fechas: %s a %s, %d huésped(es).",
                reserva.getAlojamiento().getNombre(),
                reserva.getUsuario().getNombre(),
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes()
        );
    }

    private String construirEmailNuevaReserva(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        ¡Tienes una nueva reserva!
                        
                        📋 Detalles de la reserva:
                        • Alojamiento: %s
                        • Huésped: %s
                        • Email: %s
                        • Teléfono: %s
                        • Check-in: %s
                        • Check-out: %s
                        • Huéspedes: %d
                        
                        Por favor, revisa tu panel para confirmar o rechazar la reserva.
                        
                        Saludos,
                        El equipo de la plataforma
                        """,
                reserva.getAlojamiento().getAnfitrion().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getTelefono() != null ? reserva.getUsuario().getTelefono() : "No disponible",
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes()
        );
    }


    private String construirEmailRecordatorio(Reserva reserva) {
        return String.format("""
                        Hola %s,
                        
                        Este es un recordatorio amistoso de que tu check-in es mañana.
                        
                        📍 %s
                        %s
                        
                        ¡Que tengas un excelente viaje!
                        
                        Saludos,
                        El equipo de la plataforma
                        """,
                reserva.getUsuario().getNombre(),
                reserva.getAlojamiento().getNombre(),
                reserva.getAlojamiento().getDireccion()
        );
    }
}