package application.services.login;

import application.dto.usuario.CrearUsuarioDTO;
import application.dto.auth.LoginDTO;
import application.dto.auth.TokenDTO;
import application.dto.usuario.UsuarioDTO;

public interface AuthService {
    void logout(String token);

    /** Inicia sesión: devuelve token + datos básicos. */
    TokenDTO login(LoginDTO request) throws Exception;

    /** Registro de usuario que devuelve usuario creado (sin pwd). */
    UsuarioDTO register(CrearUsuarioDTO dto) throws Exception;

    /** Validación de token (puede delegar a TokenService). */
    boolean validarToken(String token);
}

