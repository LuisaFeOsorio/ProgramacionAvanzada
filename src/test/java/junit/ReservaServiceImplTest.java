package junit;

import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.exceptions.reserva.ReservaNoCanceladaException;
import application.exceptions.reserva.ReservaNoCreadaException;
import application.mappers.ReservaMapper;
import application.model.Alojamiento;
import application.model.Reserva;
import application.model.Usuario;
import application.model.enums.EstadoReserva;
import application.repositories.AlojamientoRepository;
import application.repositories.ReservaRepository;
import application.repositories.UsuarioRepository;
import application.services.NotificacionesService;
import application.services.email.EmailService;
import application.services.impl.ReservaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlojamientoRepository alojamientoRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificacionesService notificacionesService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setEmail("juan@test.com");

        alojamiento = new Alojamiento();
        alojamiento.setId(2L);
        alojamiento.setNombre("Casa Azul");
        alojamiento.setCapacidadMaxima(4);
        alojamiento.setAnfitrion(usuario);

        reserva = new Reserva();
        reserva.setId(10L);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setCheckIn(LocalDate.now().plusDays(3));
        reserva.setCheckOut(LocalDate.now().plusDays(6));
        reserva.setNumeroHuespedes(2);
        reserva.setEstado(EstadoReserva.PENDIENTE);
    }

    //  Crear reserva correctamente
    @Test
    void crearReserva_datosValidos_creaReservaYNotifica() throws Exception {
        CrearReservaDTO dto = new CrearReservaDTO(
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes(),
                alojamiento.getId().toString(),
                List.of()
        );

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));
        when(reservaRepository.existsByAlojamientoIdAndFechasSolapadas(
                any(), any(), any(), any())).thenReturn(false);
        when(reservaRepository.save(any())).thenReturn(reserva);

        ReservaDTO reservaDTO = new ReservaDTO();
        reservaDTO.setId("10");
        when(reservaMapper.toDTO(any())).thenReturn(reservaDTO);

        ReservaDTO resultado = reservaService.crearReserva(usuario.getId().toString(), dto);

        assertNotNull(resultado);
        assertEquals("10", resultado.getId());
        verify(reservaRepository, times(1)).save(any());
        verify(emailService, atLeastOnce()).enviarConfirmacionReservaUsuario(any());
    }

    @Test
    void crearReserva_alojamientoNoDisponible_lanzaExcepcion() {
        CrearReservaDTO dto = new CrearReservaDTO(
                reserva.getCheckIn(),
                reserva.getCheckOut(),
                reserva.getNumeroHuespedes(),
                alojamiento.getId().toString(),
                List.of()
        );

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));
        when(reservaRepository.existsByAlojamientoIdAndFechasSolapadas(
                any(), any(), any(), any())).thenReturn(true);

        assertThrows(ReservaNoCreadaException.class,
                () -> reservaService.crearReserva(usuario.getId().toString(), dto));
    }

    //  Cancelar reserva correctamente
    @Test
    void cancelarReserva_usuarioValido_cancelaYNotifica() throws Exception {
        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        reservaService.cancelarReserva(reserva.getId().toString(), usuario.getId().toString());

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        verify(reservaRepository, times(1)).save(reserva);
        verify(notificacionesService, atLeastOnce()).crearNotificacionAnfitrion(any(), any(), any());
    }

    //  No se puede cancelar una reserva ya cancelada
    @Test
    void cancelarReserva_yaCancelada_lanzaExcepcion() {
        reserva.setEstado(EstadoReserva.CANCELADA);

        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        assertThrows(ReservaNoCanceladaException.class,
                () -> reservaService.cancelarReserva(reserva.getId().toString(), usuario.getId().toString()));
    }
}
