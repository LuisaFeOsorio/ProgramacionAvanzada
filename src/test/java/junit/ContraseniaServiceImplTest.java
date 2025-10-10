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

    // --- TEST 1 ---
    @Test
    void solicitarCodigoRecuperacion_usuarioExiste_enviaEmail() {
        // Mock: el usuario sí existe
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        // Ejecutar método
        contraseniaService.solicitarCodigoRecuperacion("test@example.com");

        // Verificar que se envió email
        verify(emailService, times(1)).enviarEmail(
                eq("test@example.com"),
                eq("Código de recuperación de contraseña"),
                contains("Has solicitado restablecer tu contraseña")
        );
    }

    // --- TEST 2 ---
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

    // --- TEST 3 ---
    @Test
    void restablecerContrasena_codigoValido_actualizaContrasenaYEnviaEmail() {
        // Paso 1: simular usuario y código válido
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        contraseniaService.solicitarCodigoRecuperacion("test@example.com");

        // Obtener el código generado (usando reflexión)
        String codigo = obtenerCodigoGenerado("test@example.com");

        when(passwordEncoder.encode("NuevaPass123")).thenReturn("hashed123");

        // Paso 2: ejecutar restablecimiento
        contraseniaService.restablecerContrasena("test@example.com", codigo, "NuevaPass123");

        // Verificar que se guardó el usuario con contraseña nueva
        verify(usuarioRepository).save(argThat(u -> u.getContrasenia().equals("hashed123")));

        // Verificar que se envió email de confirmación
        verify(emailService).enviarEmail(eq("test@example.com"), eq("Contraseña restablecida exitosamente"), anyString());
    }

    // --- TEST 4 ---
    @Test
    void restablecerContrasena_codigoInvalido_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        // No se genera código previamente, entonces es inválido
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contraseniaService.restablecerContrasena("test@example.com", "000000", "NuevaPass123")
        );

        assertEquals("Código de recuperación inválido o expirado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // --- TEST 5 ---
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

    // --- MÉTODO AUXILIAR ---
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
