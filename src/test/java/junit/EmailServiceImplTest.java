package junit;

import application.model.Alojamiento;
import application.model.Reserva;
import application.model.Usuario;
import application.services.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private Mailer mailer;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Reserva reserva;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Usuario usuario = new Usuario();
        usuario.setNombre("Luisa");
        usuario.setEmail("luisa@example.com");
        usuario.setTelefono("12345");

        Usuario anfitrion = new Usuario();
        anfitrion.setNombre("Carlos");
        anfitrion.setEmail("carlos@host.com");
        anfitrion.setTelefono("98765");

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa Bonita");
        alojamiento.setDireccion("Calle 123");
        alojamiento.setAnfitrion(anfitrion);

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setNumeroHuespedes(3);
        reserva.setCheckIn(LocalDate.of(2025, 10, 15));
        reserva.setCheckOut(LocalDate.of(2025, 10, 20));
    }

    @Test
    void enviarConfirmacionReserva_enviaEmailCorrectamente() {
        emailService.enviarConfirmacionReserva(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarNotificacionNuevaReservaAnfitrion_enviaEmailCorrectamente() {
        emailService.enviarNotificacionNuevaReservaAnfitrion(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarNotificacionCancelacionUsuario_enviaEmailCorrectamente() {
        emailService.enviarNotificacionCancelacionUsuario(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarNotificacionCancelacionAnfitrion_enviaEmailCorrectamente() {
        emailService.enviarNotificacionCancelacionAnfitrion(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarNotificacionAprobacionReserva_enviaEmailCorrectamente() {
        emailService.enviarNotificacionAprobacionReserva(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarNotificacionRechazoReserva_enviaEmailCorrectamente() {
        emailService.enviarNotificacionRechazoReserva(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarRecordatorioCheckIn_enviaEmailCorrectamente() {
        emailService.enviarRecordatorioCheckIn(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void enviarSolicitudComentario_enviaEmailCorrectamente() {
        emailService.enviarSolicitudComentario(reserva);
        verify(mailer, times(1)).sendMail(any(Email.class));
    }

    @Test
    void validarEmail_valido_devuelveTrue() {
        assert (emailService.validarEmail("test@domain.com"));
    }

    @Test
    void validarEmail_invalido_devuelveFalse() {
        assert (!emailService.validarEmail("correo-invalido"));
        assert (!emailService.validarEmail(""));
        assert (!emailService.validarEmail(null));
    }

    @Test
    void enviarEmail_generico_enviaEmailCorrectamente() {
        emailService.enviarEmail("test@correo.com", "Asunto", "Contenido");
        verify(mailer, times(1)).sendMail(any(Email.class));
    }
}
