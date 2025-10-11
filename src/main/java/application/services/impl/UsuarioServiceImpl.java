package application.services.impl;

import application.dto.contrasenia.CambioContraseniaDTO;
import application.dto.usuario.*;
import application.dto.usuario.VolverseAnfitrionDTO;
import application.dto.usuario.ActualizarAnfitrionDTO;
import application.exceptions.usuario.EmailEnUsoException;
import application.exceptions.usuario.UsuarioNoEncontradoException;
import application.mappers.UsuarioMapper;
import application.model.Usuario;
import application.model.enums.Role;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import application.repositories.UsuarioRepository;
import application.services.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioMapper usuarioMapping;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UsuarioDTO crear(CrearUsuarioDTO usuarioDTO) throws EmailEnUsoException {

        if (usuarioRepository.existsByEmail(usuarioDTO.email())) {
            throw new EmailEnUsoException("El email ya está registrado");
        }
        Usuario usuario = usuarioMapping.toEntity(usuarioDTO);
        usuario.setContrasenia(passwordEncoder.encode(usuarioDTO.contrasenia()));
        usuario.setActivo(true);
        usuario.setRol(Role.USUARIO);
        usuario.setDocumentosVerificados(false);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapping.toDTO(usuarioGuardado);

    }

    @Override
    public UsuarioDTO obtenerPorId(String id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));
        return usuarioMapping.toDTO(usuario);

    }

    @Override
    public void eliminar(String id) throws UsuarioNoEncontradoException {
        if (!usuarioRepository.existsById(Long.valueOf(id))) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(Long.valueOf(id));
    }


    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos(String nombre, String email, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);

        List<Usuario> usuarios;

        if (nombre != null && email != null) {
            usuarios = usuarioRepository.findByNombreContainingAndEmailContaining(nombre, email, pageable);
        } else if (nombre != null) {
            usuarios = usuarioRepository.findByNombreContaining(nombre, pageable);
        } else if (email != null) {
            usuarios = usuarioRepository.findByEmailContaining(email, pageable);
        } else {
            usuarios = usuarioRepository.findAll(pageable).getContent();
        }

        return usuarios.stream()
                .map(usuarioMapping::toDTO)
                .collect(Collectors.toList());
    }


    // En UsuarioServiceImpl
    @Override
    public UsuarioDTO obtenerPorEmail(String email) {
        System.out.println("🔍 Buscando usuario por email: " + email);

        Usuario usuario = null;
        try {
            usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        System.out.println("❌ Usuario no encontrado con email: " + email);
                        return new UsuarioNoEncontradoException("Usuario no encontrado");
                    });
        } catch (UsuarioNoEncontradoException e) {
            throw new RuntimeException(e);
        }

        System.out.println("✅ Usuario encontrado: " + usuario.getNombre());
        return usuarioMapping.toDTO(usuario);
    }

    // ✅ ACTUALIZAR USUARIO
    @Override
    public UsuarioDTO actualizar(String id, EditarUsuarioDTO usuarioDTO) throws EmailEnUsoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // Validar que el nuevo email no exista en otro usuario (si se está cambiando)
        if (usuarioDTO.email() != null && !usuario.getEmail().equals(usuarioDTO.email())) {
            if (usuarioRepository.existsByEmail(usuarioDTO.email())) {
                throw new EmailEnUsoException("El email ya está en uso por otro usuario");
            }
        }

        // Actualizar entidad usando el mapper
        usuarioMapping.updateEntityFromDTO(usuarioDTO, usuario);

        // Si se cambió el email, actualizarlo (el mapper lo ignora por seguridad)
        if (usuarioDTO.email() != null) {
            usuario.setEmail(usuarioDTO.email());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapping.toDTO(usuarioActualizado);
    }

    @Override
    public void cambiarContrasenia(String id, CambioContraseniaDTO cambioContraseniaDTO)throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        // Validar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(cambioContraseniaDTO.contraseniaActual(), usuario.getContrasenia())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(cambioContraseniaDTO.nuevaContrasenia(), usuario.getContrasenia())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }

        usuario.setContrasenia(passwordEncoder.encode(cambioContraseniaDTO.nuevaContrasenia()));
        usuarioRepository.save(usuario);
    }

    // ✅ CAMBIAR ESTADO (ACTIVAR/DESACTIVAR)
    @Override
    public UsuarioDTO cambiarEstado(String id)throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setActivo(!usuario.getActivo());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapping.toDTO(usuarioActualizado);
    }

    // ✅ VOLVERSE ANFITRIÓN
    @Override
    public UsuarioDTO volverseAnfitrion(String id, VolverseAnfitrionDTO anfitrionDTO) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        // Validar que no sea ya anfitrión
        if (usuario.getRol() == Role.ANFITRION) {
            throw new IllegalArgumentException("El usuario ya es anfitrión");
        }

        // Cambiar rol a anfitrión y actualizar información
        usuario.setRol(Role.ANFITRION);
        usuario.setDescripcionPersonal(anfitrionDTO.descripcionPersonal());
        usuario.setDocumentoIdentidad(anfitrionDTO.documentoIdentidad());
        usuario.setArchivoDocumentos(anfitrionDTO.archivoDocumentos());
        usuario.setDocumentosVerificados(false); // Inicialmente no verificados

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapping.toDTO(usuarioActualizado);
    }

    // ✅ ACTUALIZAR INFORMACIÓN DE ANFITRIÓN
    @Override
    public UsuarioDTO actualizarInformacionAnfitrion(String id, ActualizarAnfitrionDTO anfitrionDTO) throws UsuarioNoEncontradoException{
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        // Validar que sea anfitrión
        if (usuario.getRol() != Role.ANFITRION) {
            throw new IllegalArgumentException("Solo los anfitriones pueden actualizar esta información");
        }

        // Actualizar solo los campos proporcionados
        if (anfitrionDTO.descripcionPersonal() != null) {
            usuario.setDescripcionPersonal(anfitrionDTO.descripcionPersonal());
        }
        if (anfitrionDTO.documentoIdentidad() != null) {
            usuario.setDocumentoIdentidad(anfitrionDTO.documentoIdentidad());
        }
        if (anfitrionDTO.archivoDocumentos() != null) {
            usuario.setArchivoDocumentos(anfitrionDTO.archivoDocumentos());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapping.toDTO(usuarioActualizado);
    }

    // ✅ VERIFICAR DOCUMENTOS (SOLO ADMIN)
    @Override
    public UsuarioDTO verificarDocumentos(String id, boolean verificados)throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        // Validar que sea anfitrión
        if (usuario.getRol() != Role.ANFITRION) {
            throw new IllegalArgumentException("Solo los anfitriones tienen documentos para verificar");
        }

        usuario.setDocumentosVerificados(verificados);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapping.toDTO(usuarioActualizado);
    }

    // ✅ OBTENER ANFITRIONES
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerAnfitriones(boolean soloVerificados) {
        List<Usuario> anfitriones;

        if (soloVerificados) {
            anfitriones = usuarioRepository.findByRolAndDocumentosVerificadosTrue(Role.ANFITRION);
        } else {
            anfitriones = usuarioRepository.findByRol(Role.ANFITRION);
        }

        return anfitriones.stream()
                .map(usuarioMapping::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ BUSCAR POR ROL
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarPorRol(String rol) {
        try {
            Role roleEnum = Role.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioRepository.findByRol(roleEnum);

            return usuarios.stream()
                    .map(usuarioMapping::toDTO)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }
    }

    // ✅ MÉTODOS DE CONSULTA ADICIONALES
    @Transactional(readOnly = true)
    public boolean esAnfitrion(String id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        return usuario.getRol() == Role.ANFITRION;
    }

    @Transactional(readOnly = true)
    public boolean tieneDocumentosVerificados(String  id) throws UsuarioNoEncontradoException{
        Usuario usuario = usuarioRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        return Boolean.TRUE.equals(usuario.getDocumentosVerificados());
    }

}