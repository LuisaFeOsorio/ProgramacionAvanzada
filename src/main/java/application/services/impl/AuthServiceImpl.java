package application.services.impl;


import application.dto.auth.LoginDTO;
import application.dto.auth.TokenDTO;
import application.dto.usuario.CrearUsuarioDTO;
import application.dto.usuario.UsuarioDTO;
import application.model.entidades.Usuario;
import application.model.enums.Role;
import application.repositories.UsuarioRepository;
import application.security.JWTUtils;
import application.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO login(LoginDTO request) throws Exception {
        Optional<Usuario> optionalUser = userRepository.findByEmail(request.email());

        if (optionalUser.isEmpty()) {
            throw new Exception("El usuario no existe");
        }

        Usuario user = optionalUser.get();

        if (!passwordEncoder.matches(request.contrasena(), user.getContrasenia())) {
            throw new Exception("El usuario o contraseña no son válidos");
        }

        String token = jwtUtils.generateToken(user.getId().toString(), createClaims(user));
        return new TokenDTO(token);
    }

    @Override
    public UsuarioDTO register(CrearUsuarioDTO dto) throws Exception {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new Exception("El usuario ya existe");
        }

        Usuario user = new Usuario();
        user.setNombre(dto.nombre());
        user.setEmail(dto.email());
        user.setTelefono(dto.telefono());
        user.setContrasenia(passwordEncoder.encode(dto.contrasenia()));
        user.setFotoPerfil(dto.fotoPerfil());
        user.setFechaNacimiento(dto.fechaNacimiento());
        user.setRol(dto.rol() != null ? dto.rol() : Role.GUEST);

        user = userRepository.save(user);

        return new UsuarioDTO(
                user.getNombre(),
                user.getEmail(),
                user.getTelefono(),
                user.getContrasenia(),
                user.getFotoPerfil(),
                user.getFechaNacimiento(),
                user.getRol()
        );
    }

    @Override
    public void logout(String token) {
        // Si manejas lista de tokens inválidos, podrías guardarlo ahí
        // Aquí solo se deja como placeholder
    }


    @Override
    public boolean validarToken(String token) {
        return jwtUtils.validateToken(token);
    }

    private Map<String, String> createClaims(Usuario user) {
        return Map.of(
                "email", user.getEmail(),
                "name", user.getNombre(),
                "role", "ROLE_" + user.getRol().name()
        );
    }
}
