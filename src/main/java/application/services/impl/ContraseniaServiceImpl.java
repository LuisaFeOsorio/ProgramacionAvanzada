package application.services.impl;

import application.model.Usuario;
import application.repositories.UsuarioRepository;
import application.services.contrasenia.ContraseniaService;
import application.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ContraseniaServiceImpl implements ContraseniaService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, CodigoRecuperacion> codigosRecuperacion = new ConcurrentHashMap<>();
    private static final int LONGITUD_CODIGO = 6;
    private static final int TIEMPO_EXPIRACION_MINUTOS = 15;

    @Autowired
    public ContraseniaServiceImpl(UsuarioRepository usuarioRepository,
                                  EmailService emailService,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void solicitarCodigoRecuperacion(String email) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ese email"));

        // Generar código de recuperación
        String codigo = generarCodigo();
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusMinutes(TIEMPO_EXPIRACION_MINUTOS);

        // Guardar código en memoria
        codigosRecuperacion.put(email, new CodigoRecuperacion(codigo, fechaExpiracion));

        // Enviar email con el código
        enviarEmailRecuperacion(usuario, codigo);
    }
    @PostMapping("/restablecer")
    @Override
    public void restablecerContrasena(String email, String codigo, String nuevaContrasenia) {
        System.out.println("🔐 === INICIO RESTABLECER CONTRASEÑA ===");
        System.out.println("📧 Email: " + email);
        System.out.println("🔑 Código: " + codigo);
        System.out.println("🔒 Nueva contraseña: " + (nuevaContrasenia != null ? "*****" : "null"));

        try {
            // Verificar que el código es válido
            if (!verificarCodigo(email, codigo)) {
                System.out.println("❌ Código de recuperación inválido o expirado para: " + email);
                throw new IllegalArgumentException("Código de recuperación inválido o expirado");
            }
            System.out.println("✅ Código válido");

            // Validar nueva contraseña
            validarContrasena(nuevaContrasenia);
            System.out.println("✅ Nueva contraseña válida");

            // Buscar usuario
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        System.out.println("❌ Usuario no encontrado: " + email);
                        return new IllegalArgumentException("Usuario no encontrado");
                    });
            System.out.println("✅ Usuario encontrado: " + usuario.getNombre());

            // Actualizar contraseña
            usuario.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
            usuarioRepository.save(usuario);
            System.out.println("✅ Contraseña actualizada correctamente");

            // Limpiar código usado
            codigosRecuperacion.remove(email);
            System.out.println("🧹 Código de recuperación eliminado");

            // Enviar email de confirmación
            enviarEmailConfirmacion(usuario);
            System.out.println("📧 Email de confirmación enviado");

            System.out.println("🎉 === FIN RESTABLECER CONTRASEÑA ===");

        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ ERROR CONTROLADO: " + e.getMessage());
            throw e; // Re-lanzar para que Spring devuelva 400
        } catch (Exception e) {
            System.out.println("❌ ERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al restablecer la contraseña"); // 500 Internal Server Error
        }
    }

    @Override
    public boolean verificarCodigo(String email, String codigo) {
        CodigoRecuperacion codigoRecuperacion = codigosRecuperacion.get(email);

        if (codigoRecuperacion == null) {
            return false;
        }

        // Verificar si el código ha expirado
        if (codigoRecuperacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            codigosRecuperacion.remove(email);
            return false;
        }

        // Verificar que el código coincide
        return codigoRecuperacion.getCodigo().equals(codigo);
    }

    // --- MÉTODOS PRIVADOS ---

    private String generarCodigo() {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            codigo.append(random.nextInt(10)); // Números del 0-9
        }

        return codigo.toString();
    }

    private void validarContrasena(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        // Validaciones adicionales (opcional)
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una mayúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número");
        }
    }

    private void enviarEmailRecuperacion(Usuario usuario, String codigo) {
        String asunto = "Código de recuperación de contraseña";
        String mensaje = String.format("""
                Hola %s,
                
                Has solicitado restablecer tu contraseña. 
                Tu código de recuperación es: %s
                
                Este código expirará en %d minutos.
                
                Si no solicitaste este cambio, por favor ignora este email.
                
                Saludos,
                El equipo de la aplicación
                """, usuario.getNombre(), codigo, TIEMPO_EXPIRACION_MINUTOS);

        emailService.enviarEmail(usuario.getEmail(), asunto, mensaje);
    }

    private void enviarEmailConfirmacion(Usuario usuario) {
        String asunto = "Contraseña restablecida exitosamente";
        String mensaje = String.format("""
                Hola %s,
                
                Tu contraseña ha sido restablecida exitosamente.
                
                Si no realizaste esta acción, por favor contacta con soporte inmediatamente.
                
                Saludos,
                El equipo de la aplicación
                """, usuario.getNombre());

        emailService.enviarEmail(usuario.getEmail(), asunto, mensaje);
    }

    // Clase interna para manejar los códigos de recuperación
    private static class CodigoRecuperacion {
        private final String codigo;
        private final LocalDateTime fechaExpiracion;

        public CodigoRecuperacion(String codigo, LocalDateTime fechaExpiracion) {
            this.codigo = codigo;
            this.fechaExpiracion = fechaExpiracion;
        }

        public String getCodigo() {
            return codigo;
        }

        public LocalDateTime getFechaExpiracion() {
            return fechaExpiracion;
        }
    }
}