package application.services;



import application.dto.CrearUsuarioDTO;
import application.dto.LoginDTO;
import application.dto.UserDTO;

public interface AuthService {
    /** Inicia sesión: devuelve token + datos básicos. */
    LoginDTO login(LoginDTO request);

    /** Registro de usuario que devuelve usuario creado (sin pwd). */
    UserDTO register(CrearUsuarioDTO dto);

    /** Cierra sesión (invalidar token si aplica). */
    void logout(String token);

    /** Validación de token (puede delegar a TokenService). */
    boolean validarToken(String token);
}

