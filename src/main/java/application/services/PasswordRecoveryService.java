package application.services;

public interface PasswordRecoveryService {

    void solicitarCodigoRecuperacion(String email);

    void restablecerContrasena(String email, String codigo, String nuevaPassword);

    boolean verificarCodigo(String email, String codigo);
}