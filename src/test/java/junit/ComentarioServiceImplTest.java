package junit;

import application.dto.comentario.ComentarioDTO;
import application.dto.comentario.CrearComentarioDTO;
import application.dto.comentario.RespuestaComentarioDTO;
import application.dto.paginacion.PaginacionDTO;
import application.mappers.ComentarioMapper;
import application.model.Alojamiento;
import application.model.Comentario;
import application.model.Reserva;
import application.model.Usuario;
import application.model.enums.EstadoReserva;
import application.repositories.AlojamientoRepository;
import application.repositories.ComentarioRepository;
import application.repositories.ReservaRepository;
import application.repositories.UsuarioRepository;
import application.services.impl.ComentarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceImplTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlojamientoRepository alojamientoRepository;

    @Mock
    private ComentarioMapper comentarioMapper;

    @InjectMocks
    private ComentarioServiceImpl comentarioService;

    private Usuario usuario;
    private Usuario anfitrion;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(100L);
        usuario.setNombre("Usuario Test");
        usuario.setEmail("user@test.com");

        anfitrion = new Usuario();
        anfitrion.setId(200L);
        anfitrion.setNombre("Anfitrion Test");
        anfitrion.setEmail("host@test.com");

        alojamiento = new Alojamiento();
        alojamiento.setId(50L);
        alojamiento.setNombre("Alojamiento Test");
        alojamiento.setAnfitrion(anfitrion);
        alojamiento.setCalificacionPromedio(0.0);
        alojamiento.setTotalCalificaciones(0);

        reserva = new Reserva();
        reserva.setId(300L);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setEstado(EstadoReserva.COMPLETADA);
        reserva.setCheckIn(LocalDate.now().minusDays(5));
    }

    @Test
    void crearComentario_success() {
        CrearComentarioDTO dto = new CrearComentarioDTO("Excelente estancia, todo genial.", 5);

        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(reservaRepository.findById(300L)).thenReturn(Optional.of(reserva));
        when(comentarioRepository.existsByReservaId(300L)).thenReturn(false);

        Comentario saved = new Comentario();
        saved.setId(10L);
        saved.setContenido(dto.contenido());
        saved.setCalificacion(dto.calificacion());
        saved.setUsuario(usuario);
        saved.setReserva(reserva);
        saved.setAlojamiento(alojamiento);
        saved.setFechaCreacion(LocalDateTime.now());
        saved.setActivo(true);

        when(comentarioRepository.save(any(Comentario.class))).thenReturn(saved);

        // Simular actualización de calificaciones
        when(comentarioRepository.calcularPromedioCalificacionByAlojamientoId(50L)).thenReturn(4.56);
        when(comentarioRepository.countByAlojamientoIdAndActivoTrue(50L)).thenReturn(3);
        when(alojamientoRepository.findById(50L)).thenReturn(Optional.of(alojamiento));
        when(alojamientoRepository.save(any(Alojamiento.class))).thenAnswer(inv -> inv.getArgument(0));

        ComentarioDTO dtoResp = new ComentarioDTO(
                10L,
                saved.getContenido(),
                saved.getCalificacion(),
                null,
                saved.getFechaCreacion(),
                null,
                true,
                usuario.getNombre(),
                null,
                usuario.getId(),
                alojamiento.getNombre(),
                alojamiento.getId(),
                reserva.getCheckIn(),
                null,
                null
        );
        when(comentarioMapper.toDTO(saved)).thenReturn(dtoResp);

        ComentarioDTO result = comentarioService.crearComentario("100", "300", dto);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(5, result.calificacion());
        verify(comentarioRepository).save(any(Comentario.class));
        verify(alojamientoRepository).save(any(Alojamiento.class));
        verify(comentarioMapper).toDTO(saved);
    }

    @Test
    void crearComentario_usuarioNoEncontrado_lanzaIllegalArgumentException() {
        CrearComentarioDTO dto = new CrearComentarioDTO("Comentario válido", 4);
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario("999", "300", dto));
    }

    @Test
    void crearComentario_reservaNoEncontrada_lanzaIllegalArgumentException() {
        CrearComentarioDTO dto = new CrearComentarioDTO("Comentario válido", 4);
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario("100", "999", dto));
    }

    @Test
    void crearComentario_usuarioNoCoincideConReserva_lanzaIllegalArgumentException() {
        CrearComentarioDTO dto = new CrearComentarioDTO("Comentario válido", 4);

        Usuario otro = new Usuario();
        otro.setId(777L);

        reserva.setUsuario(otro); // reserva hecha por otro usuario

        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(reservaRepository.findById(300L)).thenReturn(Optional.of(reserva));

        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario("100", "300", dto));
    }

    @Test
    void crearComentario_reservaNoCompletada_lanzaIllegalArgumentException() {
        CrearComentarioDTO dto = new CrearComentarioDTO("Comentario válido", 4);

        reserva.setEstado(EstadoReserva.CONFIRMADA); // no completada

        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(reservaRepository.findById(300L)).thenReturn(Optional.of(reserva));

        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario("100", "300", dto));
    }

    @Test
    void crearComentario_comentarioYaExiste_lanzaIllegalArgumentException() {
        CrearComentarioDTO dto = new CrearComentarioDTO("Comentario válido", 4);

        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(reservaRepository.findById(300L)).thenReturn(Optional.of(reserva));
        when(comentarioRepository.existsByReservaId(300L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario("100", "300", dto));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void crearComentario_contenidoInvalido_lanzaIllegalArgumentException() {
        // contenido muy corto (<10)
        CrearComentarioDTO dto = new CrearComentarioDTO("corto", 4);

        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(reservaRepository.findById(300L)).thenReturn(Optional.of(reserva));
        when(comentarioRepository.existsByReservaId(300L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario("100", "300", dto));
        verify(comentarioRepository, never()).save(any());
    }


    @Test
    void responderComentario_success() {
        Comentario comentario = new Comentario();
        comentario.setId(20L);
        comentario.setAlojamiento(alojamiento);
        comentario.setContenido("Buen lugar");
        comentario.setRespuesta(null);

        when(comentarioRepository.findById(20L)).thenReturn(Optional.of(comentario));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(anfitrion));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(inv -> inv.getArgument(0));

        RespuestaComentarioDTO respuestaDTO = new RespuestaComentarioDTO("Gracias por tu comentario");

        RespuestaComentarioDTO result = comentarioService.responderComentario("20", "200", respuestaDTO);

        assertNotNull(result);
        assertEquals("Gracias por tu comentario", result.respuesta());
        verify(comentarioRepository).save(comentario);
    }

    @Test
    void responderComentario_comentarioNoEncontrado_lanzaIllegalArgumentException() {
        when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> comentarioService.responderComentario("999", "200", new RespuestaComentarioDTO("ok")));
    }

    @Test
    void responderComentario_noEsAnfitrion_lanzaIllegalArgumentException() {
        Comentario comentario = new Comentario();
        comentario.setId(21L);
        alojamiento.setAnfitrion(anfitrion);
        comentario.setAlojamiento(alojamiento);

        Usuario otro = new Usuario();
        otro.setId(555L);

        when(comentarioRepository.findById(21L)).thenReturn(Optional.of(comentario));
        when(usuarioRepository.findById(555L)).thenReturn(Optional.of(otro));

        assertThrows(IllegalArgumentException.class,
                () -> comentarioService.responderComentario("21", "555", new RespuestaComentarioDTO("ok")));
    }

    @Test
    void responderComentario_yaTieneRespuesta_lanzaIllegalArgumentException() {
        Comentario comentario = new Comentario();
        comentario.setId(22L);
        comentario.setAlojamiento(alojamiento);
        comentario.setRespuesta("Ya respondido");

        when(comentarioRepository.findById(22L)).thenReturn(Optional.of(comentario));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(anfitrion));

        assertThrows(IllegalArgumentException.class,
                () -> comentarioService.responderComentario("22", "200", new RespuestaComentarioDTO("otra respuesta")));
    }

    @Test
    void responderComentario_respuestaVacia_lanzaIllegalArgumentException() {
        Comentario comentario = new Comentario();
        comentario.setId(23L);
        comentario.setAlojamiento(alojamiento);

        when(comentarioRepository.findById(23L)).thenReturn(Optional.of(comentario));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(anfitrion));

        assertThrows(IllegalArgumentException.class,
                () -> comentarioService.responderComentario("23", "200", new RespuestaComentarioDTO("")));
    }


    @Test
    void listarComentariosAlojamiento_success() {
        // Alojamiento existe
        when(alojamientoRepository.existsById(50L)).thenReturn(true);

        Comentario c = new Comentario();
        c.setId(400L);
        c.setContenido("Contenido largo que cumple");
        c.setCalificacion(4);
        c.setFechaCreacion(LocalDateTime.now());
        c.setActivo(true);

        Page<Comentario> page = new PageImpl<>(List.of(c), PageRequest.of(0, 10), 1);
        when(comentarioRepository.findByAlojamientoIdAndActivoTrue(eq(50L), any(Pageable.class)))
                .thenReturn(page);

        ComentarioDTO dto = new ComentarioDTO(
                400L,
                c.getContenido(),
                c.getCalificacion(),
                null,
                c.getFechaCreacion(),
                null,
                true,
                "Nombre",
                null,
                100L,
                "Alojamiento",
                50L,
                LocalDate.now(),
                null,
                null
        );
        when(comentarioMapper.toDTO(c)).thenReturn(dto);

        PaginacionDTO<ComentarioDTO> result = comentarioService.listarComentariosAlojamiento("50", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.contenido().size());
        assertEquals(400L, result.contenido().get(0).id());
    }


    @Test
    void listarComentariosAlojamiento_alojamientoNoEncontrado_lanzaIllegalArgumentException() {
        when(alojamientoRepository.existsById(999L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> comentarioService.listarComentariosAlojamiento("999", 0, 10));
    }


    @Test
    void obtenerPromedioCalificacion_success_redondeaA1Decimal() {
        when(alojamientoRepository.existsById(50L)).thenReturn(true);
        when(comentarioRepository.calcularPromedioCalificacionByAlojamientoId(50L)).thenReturn(4.333333);

        double promedio = comentarioService.obtenerPromedioCalificacion("50");

        // espera 4.3 (redondeo a 1 decimal)
        assertEquals(4.3, promedio);
    }

    @Test
    void obtenerPromedioCalificacion_alojamientoNoEncontrado_lanzaIllegalArgumentException() {
        when(alojamientoRepository.existsById(999L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> comentarioService.obtenerPromedioCalificacion("999"));
    }
}
