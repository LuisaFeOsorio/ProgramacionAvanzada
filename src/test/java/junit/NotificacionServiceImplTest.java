package junit;

import application.model.Notificacion;
import application.model.Usuario;
import application.model.enums.TipoNotificacion;
import application.repositories.NotificacionRepository;
import application.repositories.UsuarioRepository;
import application.services.email.EmailService;
import application.services.impl.NotificacionesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificacionesServiceImplTest {

    @Mock
    private EmailService emailService;

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NotificacionesServiceImpl notificacionesService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Luisa");
        usuario.setEmail("luisa@example.com");
    }

    // ✅ --- TEST: Enviar Email Genérico ---
    @Test
    void enviarEmail_exitoso_noLanzaExcepcion() {
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() ->
                notificacionesService.enviarEmail("test@correo.com", "Asunto", "Contenido")
        );

        verify(emailService, times(1)).enviarEmail(eq("test@correo.com"), eq("Asunto"), eq("Contenido"));
    }

    @Test
    void enviarEmail_conExcepcion_noRompeFlujo() {
        doThrow(new RuntimeException("Error SMTP")).when(emailService)
                .enviarEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() ->
                notificacionesService.enviarEmail("test@correo.com", "Asunto", "Contenido")
        );

        verify(emailService, times(1)).enviarEmail(anyString(), anyString(), anyString());
    }

    // ✅ --- TEST: Crear Notificación para Usuario ---
    @Test
    void crearNotificacionUsuario_exitoso_guardaNotificacion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        notificacionesService.crearNotificacionUsuario(1L, "Título", "Mensaje");

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, times(1)).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertEquals("Título", guardada.getTitulo());
        assertEquals("Mensaje", guardada.getMensaje());
        assertEquals(usuario, guardada.getUsuario());
        assertEquals(TipoNotificacion.USUARIO, guardada.getTipo());
        assertFalse(guardada.getLeida());
    }

    @Test
    void crearNotificacionUsuario_usuarioNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                notificacionesService.crearNotificacionUsuario(99L, "Título", "Mensaje")
        );

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
        verify(notificacionRepository, never()).save(any());
    }

    // ✅ --- TEST: Enviar Notificación Usuario (string userId) ---
    @Test
    void enviarNotificacionUsuario_exitoso_guardaNotificacion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        notificacionesService.enviarNotificacionUsuario("1", "Título", "Mensaje");

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void enviarNotificacionUsuario_usuarioNoExiste_noGuardaNotificacion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        notificacionesService.enviarNotificacionUsuario("1", "Título", "Mensaje");

        verify(notificacionRepository, never()).save(any());
    }

    // ✅ --- TEST: Crear Notificación para Anfitrión ---
    @Test
    void crearNotificacionAnfitrion_exitoso_guardaNotificacion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        notificacionesService.crearNotificacionAnfitrion(1L, "Título", "Mensaje");

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, times(1)).save(captor.capture());

        Notificacion guardada = captor.getValue();
        assertEquals(TipoNotificacion.ANFITRION, guardada.getTipo());
        assertEquals(usuario, guardada.getUsuario());
    }

    @Test
    void crearNotificacionAnfitrion_anfitrionNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                notificacionesService.crearNotificacionAnfitrion(1L, "Título", "Mensaje")
        );

        verify(notificacionRepository, never()).save(any());
    }

    // ✅ --- TEST: Enviar Push (solo log simulado) ---
    @Test
    void enviarPush_noLanzaExcepcion() {
        assertDoesNotThrow(() ->
                notificacionesService.enviarPush("1", "Mensaje de prueba")
        );
    }
}
