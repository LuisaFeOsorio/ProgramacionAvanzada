package application.services.impl;

import application.model.Usuario;
import application.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 UserDetailsService loading user: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found with email: " + email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        // ⭐⭐ VERIFICAR QUE EL USUARIO ESTÉ ACTIVO ⭐⭐
        if (!usuario.getActivo()) {
            System.out.println("❌ User is inactive: " + email);
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        System.out.println("✅ User found: " + usuario.getNombre());
        System.out.println("✅ User role: " + usuario.getRol());

        // ⭐⭐ CONSTRUIR UserDetails CORRECTAMENTE ⭐⭐
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContrasenia())
                .roles(usuario.getRol().name().replace("ROLE_", "")) // Remover "ROLE_" si existe
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
}