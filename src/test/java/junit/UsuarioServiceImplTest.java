package junit;

import application.dto.contrasenia.CambioContraseniaDTO;
import application.dto.usuario.*;
import application.exceptions.usuario.EmailEnUsoException;
import application.exceptions.usuario.UsuarioNoEncontradoException;
import application.mappers.UsuarioMapper;
import application.model.Usuario;
import application.model.enums.Role;
import application.repositories.UsuarioRepository;
import application.services.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private CrearUsuarioDTO crearUsuarioDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Luisa");
        usuario.setEmail("luisa@example.com");
        usuario.setContrasenia("123456");
        usuario.setRol(Role.USUARIO);
        usuario.setActivo(true);
        usuario.setFechaNacimiento(LocalDate.of(2000, 5, 10));

        usuarioDTO = new UsuarioDTO(
                "Luisa",
                "luisa@example.com",
                "12345",
                "123456",
                null,
                LocalDate.of(2000, 5, 10),
                Role.USUARIO,
                true,
                null,
                null,
                null,
                false
        );

        crearUsuarioDTO = new CrearUsuarioDTO(
                "Luisa",
                "luisa@example.com",
                "12345",
                "123456",
                null,
                LocalDate.of(2000, 5, 10),
                Role.USUARIO
        );
    }

    // ✅ CREAR USUARIO
    @Test
    void crearUsuario_datosValidos_devuelveDTO() throws Exception {
        when(usuarioRepository.existsByEmail(crearUsuarioDTO.email())).thenReturn(false);
        when(usuarioMapper.toEntity(crearUsuarioDTO)).thenReturn(usuario);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.crear(crearUsuarioDTO);

        assertNotNull(result);
        assertEquals("luisa@example.com", result.email());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void crearUsuario_emailYaExiste_lanzaEmailEnUsoException() {
        when(usuarioRepository.existsByEmail(crearUsuarioDTO.email())).thenReturn(true);

        assertThrows(EmailEnUsoException.class,
                () -> usuarioService.crear(crearUsuarioDTO));
    }

    // ✅ OBTENER POR ID
    @Test
    void obtenerPorId_existente_devuelveUsuarioDTO() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.obtenerPorId("1");

        assertNotNull(result);
        assertEquals("Luisa", result.nombre());
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.obtenerPorId("1"));
    }

    // ✅ ELIMINAR
    @Test
    void eliminar_existente_eliminaUsuario() throws Exception {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.eliminar("1");

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);
        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.eliminar("1"));
    }


    @Test
    void actualizar_datosValidos_devuelveUsuarioActualizado() throws Exception {
        EditarUsuarioDTO editarDTO = new EditarUsuarioDTO("Luisa G", "luisa@example.com", null, null, null, null,null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(any())).thenReturn(usuarioDTO);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioDTO result = usuarioService.actualizar("1", editarDTO);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void actualizar_emailYaEnUso_lanzaEmailEnUsoException() {
        EditarUsuarioDTO editarDTO = new EditarUsuarioDTO(
                "Luisa",
                "otro@example.com",
                null, null, null, null, null
        );

        // Usuario con un email diferente
        usuario.setEmail("original@example.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("otro@example.com")).thenReturn(true);

        assertThrows(EmailEnUsoException.class,
                () -> usuarioService.actualizar("1", editarDTO));

        verify(usuarioRepository, times(1)).existsByEmail("otro@example.com");
    }

    @Test
    void cambiarContrasenia_valida_actualizaContrasenia() throws Exception {
        CambioContraseniaDTO cambioDTO = new CambioContraseniaDTO("old", "new");
        usuario.setContrasenia("encodedOld");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoder.matches("new", "encodedOld")).thenReturn(false);
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        usuarioService.cambiarContrasenia("1", cambioDTO);

        verify(usuarioRepository, times(1)).save(argThat(u ->
                u.getContrasenia().equals("encodedNew")
        ));
    }


    @Test
    void cambiarContrasenia_actualIncorrecta_lanzaExcepcion() {
        CambioContraseniaDTO cambioDTO = new CambioContraseniaDTO("wrong", "new");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", usuario.getContrasenia())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cambiarContrasenia("1", cambioDTO));
    }

    // ✅ CAMBIAR ESTADO
    @Test
    void cambiarEstado_invierteActivo() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toDTO(any())).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.cambiarEstado("1");

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any());
    }

    // ✅ VOLVERSE ANFITRIÓN
    @Test
    void volverseAnfitrion_valido_cambiaRol() throws Exception {
        VolverseAnfitrionDTO dto = new VolverseAnfitrionDTO("desc", "CC123", "archivo.pdf");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toDTO(any())).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.volverseAnfitrion("1", dto);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void volverseAnfitrion_yaEsAnfitrion_lanzaExcepcion() {
        usuario.setRol(Role.ANFITRION);
        VolverseAnfitrionDTO dto = new VolverseAnfitrionDTO("desc", "CC123", "archivo.pdf");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.volverseAnfitrion("1", dto));
    }

    // ✅ VERIFICAR DOCUMENTOS
    @Test
    void verificarDocumentos_valido_actualizaCampo() throws Exception {
        usuario.setRol(Role.ANFITRION);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toDTO(any())).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.verificarDocumentos("1", true);

        assertNotNull(result);
        verify(usuarioRepository).save(any());
    }

    @Test
    void verificarDocumentos_noAnfitrion_lanzaExcepcion() {
        usuario.setRol(Role.USUARIO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.verificarDocumentos("1", true));
    }
}
