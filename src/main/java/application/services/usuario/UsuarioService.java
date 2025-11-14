package application.services.usuario;

import application.dto.contrasenia.CambioContraseniaDTO;
import application.dto.usuario.*;
import application.exceptions.ValueConflictException;
import application.exceptions.usuario.EmailEnUsoException;
import application.exceptions.usuario.UsuarioNoEncontradoException;
import application.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Optional<Usuario> findById(Long id);

    UsuarioDTO crear(CrearUsuarioDTO usuarioDTO) throws ValueConflictException, EmailEnUsoException;

    UsuarioDTO obtenerPorId(String id) throws UsuarioNoEncontradoException;

    void eliminar(String id) throws UsuarioNoEncontradoException;

    List<UsuarioDTO> obtenerTodos(String nombre, String correo, int pagina, int tamanio
    );


    UsuarioDTO actualizarUsuario(Long id, EditarUsuarioDTO dto);

    void cambiarContrasenia(String id, CambioContraseniaDTO cambioContraseniaDTO) throws UsuarioNoEncontradoException;

    UsuarioDTO cambiarEstado(String id) throws UsuarioNoEncontradoException;

    boolean existePorEmail(String email);

    void restablecerContrasenia(String email, String nuevaContrasenia);

    UsuarioDTO volverseAnfitrion(String id, VolverseAnfitrionDTO anfitrionDTO) throws UsuarioNoEncontradoException;

    UsuarioDTO actualizarInformacionAnfitrion(String id, ActualizarAnfitrionDTO anfitrionDTO) throws UsuarioNoEncontradoException;

    UsuarioDTO verificarDocumentos(String id, boolean verificados) throws UsuarioNoEncontradoException;

    List<UsuarioDTO> obtenerAnfitriones(boolean soloVerificados);

    List<UsuarioDTO> buscarPorRol(String rol);

    boolean esAnfitrion(String id) throws UsuarioNoEncontradoException;

    UsuarioDTO obtenerPorEmail(String email);

    Usuario findByEmail(String email) throws UsuarioNoEncontradoException;
}