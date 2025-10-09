package application.services.usuario;

import java.util.List;
import application.dto.contrasenia.CambioContraseniaDTO;
import application.dto.usuario.*;
import application.exceptions.ValueConflictException;
import application.exceptions.usuario.EmailEnUsoException;
import application.exceptions.usuario.UsuarioNoEncontradoException;

public interface UsuarioService {

    UsuarioDTO crear(CrearUsuarioDTO usuarioDTO) throws ValueConflictException, EmailEnUsoException;

    UsuarioDTO obtenerPorId(String id) throws UsuarioNoEncontradoException;

    void eliminar(String id)throws UsuarioNoEncontradoException;

    List<UsuarioDTO> obtenerTodos(String nombre, String correo, int pagina, int tamanio
    );
    UsuarioDTO actualizar(String id, EditarUsuarioDTO usuarioDTO) throws EmailEnUsoException;

    void cambiarContrasenia(String id, CambioContraseniaDTO cambioContraseniaDTO) throws UsuarioNoEncontradoException;

    UsuarioDTO cambiarEstado(String id)throws UsuarioNoEncontradoException;

    UsuarioDTO volverseAnfitrion(String id, VolverseAnfitrionDTO anfitrionDTO)throws UsuarioNoEncontradoException;

    UsuarioDTO actualizarInformacionAnfitrion(String id, ActualizarAnfitrionDTO anfitrionDTO) throws UsuarioNoEncontradoException;

    UsuarioDTO verificarDocumentos(String id, boolean verificados) throws UsuarioNoEncontradoException;

    List<UsuarioDTO> obtenerAnfitriones(boolean soloVerificados);

    List<UsuarioDTO> buscarPorRol(String rol);

    boolean esAnfitrion(String id) throws UsuarioNoEncontradoException;
}