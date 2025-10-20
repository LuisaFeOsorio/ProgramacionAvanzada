package junit;

import application.model.Usuario;
import application.repositories.UsuarioRepository;
import application.services.email.EmailService;
import application.services.impl.ContraseniaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ContraseniaServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContraseniaServiceImpl contraseniaService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");
        usuario.setNombre("Luisa");
        usuario.setContrasenia("123456");
    }


    @Test
    void solicitarCodigoRecuperacion_usuarioExiste_enviaEmail() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        contraseniaService.solicitarCodigoRecuperacion("test@example.com");
        verify(emailService, times(1)).enviarEmail(
                eq("test@example.com"),
                eq("Código de recuperación de contraseña"),
                contains("Has solicitado restablecer tu contraseña")
        );
    }

    @Test
    void solicitarCodigoRecuperacion_usuarioNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contraseniaService.solicitarCodigoRecuperacion("noexiste@example.com")
        );

        assertEquals("No existe un usuario con ese email", ex.getMessage());
        verify(emailService, never()).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void restablecerContrasena_codigoValido_actualizaContrasenaYEnviaEmail() {

        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        contraseniaService.solicitarCodigoRecuperacion("test@example.com");
        String codigo = obtenerCodigoGenerado("test@example.com");
        when(passwordEncoder.encode("NuevaPass123")).thenReturn("hashed123");
        contraseniaService.restablecerContrasena("test@example.com", codigo, "NuevaPass123");
        verify(usuarioRepository).save(argThat(u -> u.getContrasenia().equals("hashed123")));
        verify(emailService).enviarEmail(eq("test@example.com"), eq("Contraseña restablecida exitosamente"), anyString());
    }

    @Test
    void restablecerContrasena_codigoInvalido_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contraseniaService.restablecerContrasena("test@example.com", "000000", "NuevaPass123")
        );

        assertEquals("Código de recuperación inválido o expirado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void validarContrasena_invalida_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        contraseniaService.solicitarCodigoRecuperacion("test@example.com");
        String codigo = obtenerCodigoGenerado("test@example.com");

        // Contraseña muy corta
        assertThrows(IllegalArgumentException.class, () ->
                contraseniaService.restablecerContrasena("test@example.com", codigo, "abc")
        );
    }

    private String obtenerCodigoGenerado(String email) {
        try {
            var field = ContraseniaServiceImpl.class.getDeclaredField("codigosRecuperacion");
            field.setAccessible(true);
            var map = (java.util.Map<String, ?>) field.get(contraseniaService);
            var codigoObj = map.get(email);

            var codigoField = codigoObj.getClass().getDeclaredField("codigo");
            codigoField.setAccessible(true);
            return (String) codigoField.get(codigoObj);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el código generado", e);
        }
    }
}
