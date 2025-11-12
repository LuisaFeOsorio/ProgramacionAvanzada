//package junit;
//
//import application.dto.alojamiento.AlojamientoDTO;
//import application.dto.alojamiento.CrearAlojamientoDTO;
//import application.dto.alojamiento.EditarAlojamientoDTO;
//import application.dto.alojamiento.FiltroBusquedaDTO;
//import application.dto.paginacion.PaginacionDTO;
//import application.exceptions.alojamiento.CrearAlojamientoException;
//import application.exceptions.alojamiento.EditarAlojamientoException;
//import application.exceptions.alojamiento.ObtenerAlojamientoException;
//import application.mappers.AlojamientoMapper;
//import application.model.Alojamiento;
//import application.model.Reserva;
//import application.model.Usuario;
//import application.model.enums.EstadoReserva;
//import application.model.enums.Role;
//import application.repositories.AlojamientoRepository;
//import application.repositories.UsuarioRepository;
//import application.services.impl.AlojamientoServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AlojamientoServiceImplTest {
//
//    @Mock
//    private AlojamientoRepository alojamientoRepository;
//
//    @Mock
//    private UsuarioRepository usuarioRepository;
//
//    @Mock
//    private AlojamientoMapper alojamientoMapper;
//
//    @InjectMocks
//    private AlojamientoServiceImpl alojamientoService;
//
//    private Usuario anfitrion;
//    private Usuario noAnfitrion;
//
//    @BeforeEach
//    void setup() {
//        anfitrion = new Usuario();
//        anfitrion.setId(1L);
//        anfitrion.setNombre("Host");
//        anfitrion.setEmail("host@example.com");
//        anfitrion.setRol(Role.ANFITRION);
//
//        noAnfitrion = new Usuario();
//        noAnfitrion.setId(2L);
//        noAnfitrion.setNombre("User");
//        noAnfitrion.setEmail("user@example.com");
//        noAnfitrion.setRol(Role.USUARIO);
//    }
//
//    @Test
//    void crearAlojamiento_success() throws CrearAlojamientoException {
//
//        CrearAlojamientoDTO dto = new CrearAlojamientoDTO(
//                "Casa prueba",
//                "Descripción bonita",
//                null,
//                "CiudadX",
//                "PaisX",
//                "Direccion 123",
//                150000.0,
//                4,
//                2,
//                1,
//                List.of("WiFi", "Parking"),
//                List.of("img1.jpg", "img2.jpg")
//        );
//
//        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(anfitrion));
//
//        Alojamiento saved = new Alojamiento();
//        saved.setId(10L);
//        saved.setNombre(dto.nombre());
//        saved.setAnfitrion(anfitrion);
//        saved.setActivo(true);
//        saved.setImagenes(dto.imagenes());
//        saved.setServicios(dto.servicios());
//        when(alojamientoRepository.save(any(Alojamiento.class))).thenReturn(saved);
//
//        AlojamientoDTO dtoResp = new AlojamientoDTO(
//                10L,
//                "Casa prueba",
//                "Descripción bonita",
//                "CiudadX",
//                150000.0,
//                4,
//                List.of("img1.jpg", "img2.jpg"),
//                List.of("WiFi", "Parking")
//        );
//        when(alojamientoMapper.toDTO(saved)).thenReturn(dtoResp);
//
//        AlojamientoDTO result = alojamientoService.crearAlojamiento("1", dto);
//
//        assertNotNull(result);
//        assertEquals(10L, result.id());
//        assertEquals("Casa prueba", result.nombre());
//
//        verify(usuarioRepository, times(1)).findById(1L);
//        verify(alojamientoRepository, times(1)).save(any(Alojamiento.class));
//        verify(alojamientoMapper, times(1)).toDTO(saved);
//    }
//
//    @Test
//    void crearAlojamiento_usuarioNoEncontrado_lanzaCrearAlojamientoException() {
//        CrearAlojamientoDTO dto = new CrearAlojamientoDTO(
//                "Casa prueba", "Desc", null, "Ciudad", "Pais",
//                "Dir", 100.0, 2, 1, 1, null, null);
//
//        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(CrearAlojamientoException.class, () -> alojamientoService.crearAlojamiento("99", dto));
//        verify(alojamientoRepository, never()).save(any());
//    }
//
//    @Test
//    void crearAlojamiento_usuarioNoEsAnfitrion_lanzaIllegalArgumentException() {
//        CrearAlojamientoDTO dto = new CrearAlojamientoDTO(
//                "Casa prueba", "Desc", null, "Ciudad", "Pais",
//                "Dir", 100.0, 2, 1, 1, null, null);
//
//        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(noAnfitrion));
//
//        assertThrows(IllegalArgumentException.class, () -> alojamientoService.crearAlojamiento("2", dto));
//        verify(alojamientoRepository, never()).save(any());
//    }
//
//    @Test
//    void crearAlojamiento_datosInvalidos_lanzaIllegalArgumentException() {
//        // nombre null (validación en validarDatosAlojamiento)
//        //en este test estamos buscando que falle cuando no se validan los datos de forma correcta
//        CrearAlojamientoDTO dto = new CrearAlojamientoDTO(
//                null, "Desc", null, "Ciudad", "Pais",
//                "Dir", 100.0, 2, 1, 1, null, null);
//
//        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(anfitrion));
//
//        assertThrows(IllegalArgumentException.class, () -> alojamientoService.crearAlojamiento("1", dto));
//        verify(alojamientoRepository, never()).save(any());
//    }
//
//    @Test
//    void obtenerAlojamiento_success() throws ObtenerAlojamientoException {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(5L);
//        alojamiento.setActivo(true);
//        alojamiento.setNombre("Casa");
//
//        when(alojamientoRepository.findById(5L)).thenReturn(Optional.of(alojamiento));
//
//        AlojamientoDTO dto = new AlojamientoDTO(
//                5L,
//                "Casa",
//                "Descripción",
//                "Ciudad",
//                100.0,
//                2,
//                Collections.emptyList(),
//                Collections.emptyList()
//        );
//        when(alojamientoMapper.toDTO(alojamiento)).thenReturn(dto);
//
//        AlojamientoDTO resp = alojamientoService.obtenerAlojamiento("5");
//        assertEquals(5L, resp.id());
//        assertEquals("Casa", resp.nombre());
//    }
//
//    @Test
//    void obtenerAlojamiento_noEncontrado_lanzaObtenerAlojamientoException() {
//        when(alojamientoRepository.findById(100L)).thenReturn(Optional.empty());
//        assertThrows(ObtenerAlojamientoException.class, () -> alojamientoService.obtenerAlojamiento("100"));
//    }
//
//    @Test
//    void obtenerAlojamiento_inactivo_lanzaIllegalArgumentException() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(6L);
//        alojamiento.setActivo(false);
//
//        when(alojamientoRepository.findById(6L)).thenReturn(Optional.of(alojamiento));
//        assertThrows(IllegalArgumentException.class, () -> alojamientoService.obtenerAlojamiento("6"));
//    }
//
//
//    @Test
//    void editarAlojamiento_success() throws EditarAlojamientoException {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(7L);
//        alojamiento.setActivo(true);
//        alojamiento.setNombre("Antes");
//
//        when(alojamientoRepository.findById(7L)).thenReturn(Optional.of(alojamiento));
//        when(alojamientoRepository.save(any(Alojamiento.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        // EditarAlojamientoDTO: nombre, descripcion, precioPorNoche, capacidadMaxima, numeroHabitaciones, numeroBanos, servicios
//        EditarAlojamientoDTO dto = new EditarAlojamientoDTO("Despues", null, 200.0, 5, null, null, null);
//
//        AlojamientoDTO mapped = new AlojamientoDTO(
//                7L,
//                "Despues",
//                "Descripcion",
//                "Ciudad",
//                200.0,
//                5,
//                Collections.emptyList(),
//                Collections.emptyList()
//        );
//        when(alojamientoMapper.toDTO(any(Alojamiento.class))).thenReturn(mapped);
//
//        AlojamientoDTO result = alojamientoService.editarAlojamiento("7", dto);
//
//        assertNotNull(result);
//        assertEquals(7L, result.id());
//        assertEquals("Despues", result.nombre());
//
//        verify(alojamientoRepository).save(any(Alojamiento.class));
//    }
//
//    @Test
//    void editarAlojamiento_precioInvalido_lanzaIllegalArgumentException() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(8L);
//        alojamiento.setActivo(true);
//
//        when(alojamientoRepository.findById(8L)).thenReturn(Optional.of(alojamiento));
//
//        EditarAlojamientoDTO dto = new EditarAlojamientoDTO(null, null, 0.0, null, null, null, null);
//
//        assertThrows(IllegalArgumentException.class, () -> alojamientoService.editarAlojamiento("8", dto));
//        verify(alojamientoRepository, never()).save(any());
//    }
//
//
//    @Test
//    void eliminarAlojamiento_conReservasFuturas_lanzaIllegalArgumentException() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(9L);
//        alojamiento.setActivo(true);
//
//        // Crear reserva con estado PENDIENTE
//        Reserva reserva = new Reserva();
//        reserva.setCheckIn(LocalDate.now().plusDays(10));
//        reserva.setEstado(EstadoReserva.PENDIENTE);
//
//        alojamiento.setReservas(List.of(reserva));
//
//        when(alojamientoRepository.findById(9L)).thenReturn(Optional.of(alojamiento));
//
//        assertThrows(IllegalArgumentException.class, () -> alojamientoService.eliminarAlojamiento("9"));
//        verify(alojamientoRepository, never()).save(any());
//    }
//
//    @Test
//    void eliminarAlojamiento_exito_inactivaAlojamiento() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(11L);
//        alojamiento.setActivo(true);
//        alojamiento.setReservas(Collections.emptyList());
//
//        when(alojamientoRepository.findById(11L)).thenReturn(Optional.of(alojamiento));
//        when(alojamientoRepository.save(any(Alojamiento.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        assertDoesNotThrow(() -> alojamientoService.eliminarAlojamiento("11"));
//        assertFalse(alojamiento.getActivo());
//        verify(alojamientoRepository).save(alojamiento);
//    }
//
//    @Test
//    void puedeEliminarse_devuelveFalse_siTieneReservasFuturas() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(12L);
//        Reserva r = new Reserva();
//        r.setCheckIn(LocalDate.now().plusDays(3));
//        r.setEstado(EstadoReserva.CONFIRMADA);
//        alojamiento.setReservas(List.of(r));
//
//        when(alojamientoRepository.findById(12L)).thenReturn(Optional.of(alojamiento));
//
//        boolean puede = alojamientoService.puedeEliminarse("12");
//        assertFalse(puede);
//    }
//
//    @Test
//    void puedeEliminarse_devuelveTrue_siNoTieneReservasFuturas() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(13L);
//        alojamiento.setReservas(Collections.emptyList());
//
//        when(alojamientoRepository.findById(13L)).thenReturn(Optional.of(alojamiento));
//
//        boolean puede = alojamientoService.puedeEliminarse("13");
//        assertTrue(puede);
//    }
//
//
//    @Test
//    void marcarImagenPrincipal_success_reordenaListaYPersiste() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(14L);
//        alojamiento.setImagenes(new ArrayList<>(List.of("imgA", "imgB", "imgC")));
//
//        when(alojamientoRepository.findById(14L)).thenReturn(Optional.of(alojamiento));
//        when(alojamientoRepository.save(any(Alojamiento.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        AlojamientoDTO mapped = new AlojamientoDTO(
//                14L, "Casa", "Desc", "Ciudad", 100.0, 2, alojamiento.getImagenes(), Collections.emptyList()
//        );
//        when(alojamientoMapper.toDTO(any(Alojamiento.class))).thenReturn(mapped);
//
//        AlojamientoDTO resp = alojamientoService.marcarImagenPrincipal("14", "imgC");
//
//        assertNotNull(resp);
//        assertEquals(14L, resp.id());
//
//        // verificar que la imagen 'imgC' quedó en posición 0
//        assertEquals("imgC", alojamiento.getImagenes().get(0));
//        verify(alojamientoRepository).save(alojamiento);
//        verify(alojamientoMapper).toDTO(alojamiento);
//    }
//
//    @Test
//    void marcarImagenPrincipal_imagenNoExiste_lanzaIllegalArgumentException() {
//        Alojamiento alojamiento = new Alojamiento();
//        alojamiento.setId(15L);
//        alojamiento.setImagenes(new ArrayList<>(List.of("imgX", "imgY")));
//
//        when(alojamientoRepository.findById(15L)).thenReturn(Optional.of(alojamiento));
//
//        assertThrows(IllegalArgumentException.class, () -> alojamientoService.marcarImagenPrincipal("15", "imgZ"));
//        verify(alojamientoRepository, never()).save(any());
//    }
//
//
//    @Test
//    void listarAlojamientosAnfitrion_devuelveSoloActivos() {
//        Alojamiento a1 = new Alojamiento();
//        a1.setId(21L);
//        a1.setActivo(true);
//        Alojamiento a2 = new Alojamiento();
//        a2.setId(22L);
//        a2.setActivo(false);
//
//        when(alojamientoRepository.findByAnfitrionId(1L)).thenReturn(List.of(a1, a2));
//
//        AlojamientoDTO dto1 = new AlojamientoDTO(
//                21L, "A1", "Desc", "Ciudad", 100.0, 2, Collections.emptyList(), Collections.emptyList()
//        );
//        when(alojamientoMapper.toDTO(a1)).thenReturn(dto1);
//
//        List<AlojamientoDTO> resultado = alojamientoService.listarAlojamientosAnfitrion("1");
//
//        assertEquals(1, resultado.size());
//        assertEquals(21L, resultado.get(0).id());
//    }
//
//    // ---------- buscarAlojamientos (paginado) ----------
//
//    @Test
//    void buscarAlojamientos_devuelvePaginacion() {
//        // filtro con página 0 y tamaño 10 (según tu ejemplo)
//        FiltroBusquedaDTO filtro = new FiltroBusquedaDTO(null, null, null, null, null, null, null, 0, 10);
//
//        // entidad de ejemplo
//        Alojamiento a1 = new Alojamiento();
//        a1.setId(31L);
//        a1.setActivo(true);
//
//        // construimos una Page con un solo elemento (totalElements = 1)
//        Page<Alojamiento> page = new PageImpl<>(List.of(a1), PageRequest.of(0, 10), 1);
//
//        // mock del repositorio para devolver la página
//        when(alojamientoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
//
//        // DTO mapeado esperado
//        AlojamientoDTO dto1 = new AlojamientoDTO(
//                31L,
//                "A31",
//                "Desc",
//                "Ciudad",
//                120.0,
//                3,
//                Collections.emptyList(),
//                Collections.emptyList()
//        );
//        when(alojamientoMapper.toDTO(a1)).thenReturn(dto1);
//
//        // ejecutar el método a probar
//        PaginacionDTO<AlojamientoDTO> pag = alojamientoService.buscarAlojamientos(filtro);
//
//        // verificaciones básicas
//        assertNotNull(pag);
//        assertEquals(1, pag.contenido().size(), "Debe contener 1 elemento");
//        assertEquals(31L, pag.contenido().get(0).id());
//        assertEquals(0, pag.paginaActual(), "Página actual debe ser 0");
//
//        // verificaciones adicionales según PaginacionDTO
//        assertEquals(10, pag.tamanioPagina(), "Tamaño de página debe ser 10");
//        assertEquals(1L, pag.totalElementos(), "Total de elementos debe ser 1");
//        assertEquals(1, pag.totalPaginas(), "Total de páginas debe ser 1");
//
//        // flags calculados por el constructor (page.isFirst() y page.isLast() en este caso true)
//        assertTrue(pag.primera(), "Debe ser primera página");
//        assertTrue(pag.ultima(), "Debe ser última página");
//        assertFalse(pag.tieneSiguiente(), "No debe tener siguiente página");
//        assertFalse(pag.tieneAnterior(), "No debe tener página anterior");
//
//        // verificar que se usó el mapper para convertir la entidad a DTO
//        verify(alojamientoMapper, times(1)).toDTO(a1);
//        // verificar que se llamó al repositorio
//        verify(alojamientoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
//    }
//
//}
