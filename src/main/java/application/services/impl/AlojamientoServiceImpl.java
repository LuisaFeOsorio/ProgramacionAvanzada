package application.services.impl;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.alojamiento.FiltroBusquedaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.exceptions.alojamiento.CrearAlojamientoException;
import application.exceptions.alojamiento.EditarAlojamientoException;
import application.exceptions.alojamiento.EliminarAlojamientoException;
import application.exceptions.alojamiento.ObtenerAlojamientoException;
import application.mappers.AlojamientoMapper;
import application.model.Alojamiento;
import application.model.Usuario;
import application.repositories.AlojamientoRepository;
import application.repositories.UsuarioRepository;
import application.services.alojamiento.AlojamientoService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlojamientoServiceImpl implements AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoMapper alojamientoMapper;

    @Override
    public AlojamientoDTO crear(CrearAlojamientoDTO dto) {
        Usuario anfitrion = usuarioRepository.findById(dto.anfitrionId())
                .orElseThrow(() -> new RuntimeException("El anfitrión no existe"));

        Alojamiento a = new Alojamiento();
        a.setNombre(dto.nombre());
        a.setDescripcion(dto.descripcion());
        a.setTipo(dto.tipo());
        a.setCiudad(dto.ciudad());
        a.setPais(dto.pais());
        a.setDireccion(dto.direccion());
        a.setPrecioPorNoche(dto.precioPorNoche());
        a.setCapacidadMaxima(dto.capacidadMaxima());
        a.setNumeroHabitaciones(dto.numeroHabitaciones());
        a.setNumeroBanos(dto.numeroBanos());
        a.setServicios(dto.servicios());
        a.setImagenes(dto.imagenes());
        a.setAnfitrion(anfitrion);

        // Los siguientes atributos se setean automáticamente por @PrePersist:
        // - fechaCreacion
        // - activo (true)
        // - calificacionPromedio (0.0)
        // - totalCalificaciones (0)

        alojamientoRepository.save(a);

        return alojamientoMapper.toDTO(a);
    }


    @Override
    public List<AlojamientoDTO> obtenerTodos() {
        return alojamientoRepository.findAll()
                .stream()
                .map(alojamientoMapper::toDTO) // Usar el mapper
                .toList();
    }

    @Override
    public AlojamientoDTO mapToDTO(Alojamiento a) {
        return new AlojamientoDTO(
                a.getId(),
                a.getNombre(),
                a.getDescripcion(),
                a.getTipo(),
                a.getCiudad(),
                a.getPais(),
                a.getDireccion(),
                a.getPrecioPorNoche(),
                a.getCapacidadMaxima(),
                a.getNumeroHabitaciones(),
                a.getNumeroBanos(),
                a.getServicios(),
                a.getImagenes(),
                obtenerImagenPrincipal(a), // Método helper
                a.getCalificacionPromedio(),
                a.getActivo(),
                a.getAnfitrion().getId(), // anfitrionId
                a.getFechaCreacion(),
                a.getTotalCalificaciones()
        );
    }

    private String obtenerImagenPrincipal(Alojamiento a) {
        if (a.getImagenes() == null || a.getImagenes().isEmpty()) {
            return null;
        }
        return a.getImagenes().get(0);
    }


    //  CREAR ALOJAMIENTO
    @Override
    public AlojamientoDTO crearAlojamiento(String hostId, CrearAlojamientoDTO dto) throws CrearAlojamientoException {
        try {
            Usuario anfitrion = usuarioRepository.findById(Long.valueOf(hostId))
                    .orElseThrow(() -> new CrearAlojamientoException("Usuario no encontrado"));

            if (anfitrion.getRol() != application.model.enums.Role.ANFITRION) {
                throw new IllegalArgumentException("El usuario debe ser anfitrión para crear alojamientos");
            }

            validarDatosAlojamiento(dto);

            Alojamiento alojamiento = new Alojamiento();
            alojamiento.setNombre(dto.nombre());
            alojamiento.setDescripcion(dto.descripcion());
            alojamiento.setTipo(dto.tipo());
            alojamiento.setCiudad(dto.ciudad());
            alojamiento.setPais(dto.pais());
            alojamiento.setDireccion(dto.direccion());
            alojamiento.setPrecioPorNoche(dto.precioPorNoche());
            alojamiento.setCapacidadMaxima(dto.capacidadMaxima());
            alojamiento.setNumeroHabitaciones(dto.numeroHabitaciones());
            alojamiento.setNumeroBanos(dto.numeroBanos());
            alojamiento.setAnfitrion(anfitrion);
            alojamiento.setActivo(true);
            alojamiento.setFechaCreacion(LocalDateTime.now());
            alojamiento.setServicios(dto.servicios());
            alojamiento.setImagenes(dto.imagenes());

            Alojamiento alojamientoGuardado = alojamientoRepository.save(alojamiento);
            log.info("Alojamiento creado exitosamente: {}", alojamientoGuardado.getId());

            return alojamientoMapper.toDTO(alojamientoGuardado);

        } catch (CrearAlojamientoException e) {
            log.error("Error creando alojamiento: {}", e.getMessage());
            throw e;
        }
    }


    // ✅ VALIDACIONES
    private void validarDatosAlojamiento(CrearAlojamientoDTO dto) {
        if (dto.nombre() == null || dto.nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del alojamiento es requerido");
        }
        if (dto.precioPorNoche() == null || dto.precioPorNoche() <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor a 0");
        }
        if (dto.capacidadMaxima() == null || dto.capacidadMaxima() <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor a 0");
        }
        if (dto.ciudad() == null || dto.ciudad().trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad es requerida");
        }
        if (dto.pais() == null || dto.pais().trim().isEmpty()) {
            throw new IllegalArgumentException("El país es requerido");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public AlojamientoDTO obtenerAlojamiento(String alojamientoId) throws ObtenerAlojamientoException {
        Alojamiento alojamiento = alojamientoRepository.findById(Long.valueOf(alojamientoId))
                .orElseThrow(() -> new ObtenerAlojamientoException("Alojamiento no encontrado"));

        if (!alojamiento.getActivo()) {
            throw new IllegalArgumentException("El alojamiento no está disponible");
        }

        return alojamientoMapper.toDTO(alojamiento);
    }


    @Override
    public AlojamientoDTO editarAlojamiento(String alojamientoId, EditarAlojamientoDTO dto) throws EditarAlojamientoException {
        try {
            Alojamiento alojamiento = alojamientoRepository.findById(Long.valueOf(alojamientoId))
                    .orElseThrow(() -> new EditarAlojamientoException("Alojamiento no encontrado"));

            if (!alojamiento.getActivo()) {
                throw new IllegalArgumentException("No se puede editar un alojamiento inactivo");
            }

            if (dto.nombre() != null) {
                alojamiento.setNombre(dto.nombre());
            }
            if (dto.descripcion() != null) {
                alojamiento.setDescripcion(dto.descripcion());
            }
            if (dto.precioPorNoche() != null) {
                if (dto.precioPorNoche() <= 0) {
                    throw new IllegalArgumentException("El precio por noche debe ser mayor a 0");
                }
                alojamiento.setPrecioPorNoche(dto.precioPorNoche());
            }
            if (dto.capacidadMaxima() != null) {
                if (dto.capacidadMaxima() <= 0) {
                    throw new IllegalArgumentException("La capacidad máxima debe ser mayor a 0");
                }
                alojamiento.setCapacidadMaxima(dto.capacidadMaxima());
            }
            if (dto.numeroHabitaciones() != null) {
                alojamiento.setNumeroHabitaciones(dto.numeroHabitaciones());
            }
            if (dto.numeroBanos() != null) {
                alojamiento.setNumeroBanos(dto.numeroBanos());
            }
            if (dto.servicios() != null) {
                alojamiento.setServicios(dto.servicios());
            }

            Alojamiento alojamientoActualizado = alojamientoRepository.save(alojamiento);
            log.info("Alojamiento actualizado exitosamente: {}", alojamientoId);

            return alojamientoMapper.toDTO(alojamientoActualizado);

        } catch (IllegalArgumentException e) {
            log.error("Error validando datos para editar alojamiento: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error editando alojamiento {}: {}", alojamientoId, e.getMessage());
            throw new RuntimeException("Error al editar alojamiento: " + e.getMessage());
        }
    }

    @Override
    public void eliminarAlojamiento(String alojamientoId) throws EliminarAlojamientoException {
        try {
            Alojamiento alojamiento = alojamientoRepository.findById(Long.valueOf(alojamientoId))
                    .orElseThrow(() -> new EliminarAlojamientoException("Alojamiento no encontrado"));

            if (tieneReservasFuturas(alojamiento)) {
                throw new IllegalArgumentException("No se puede eliminar un alojamiento con reservas futuras");
            }

            alojamiento.setActivo(false);
            alojamientoRepository.save(alojamiento);
            log.info("Alojamiento eliminado (inactivado): {}", alojamientoId);

        } catch (IllegalArgumentException e) {
            log.error("Error eliminando alojamiento {}: {}", alojamientoId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error eliminando alojamiento {}: {}", alojamientoId, e.getMessage());
            throw new RuntimeException("Error al eliminar alojamiento: " + e.getMessage());
        }
    }

    // ✅ VERIFICAR SI TIENE RESERVAS FUTURAS
    private boolean tieneReservasFuturas(Alojamiento alojamiento) {

        return alojamiento.getReservas().stream()
                .anyMatch(reserva ->
                        reserva.getCheckIn().isAfter(LocalDateTime.now().toLocalDate()) &&
                                (reserva.getEstado() == application.model.enums.EstadoReserva.PENDIENTE ||
                                        reserva.getEstado() == application.model.enums.EstadoReserva.CONFIRMADA)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlojamientoDTO> listarAlojamientosAnfitrion(String hostId) {
        try {
            List<Alojamiento> alojamientos = alojamientoRepository.findByAnfitrionId(Long.valueOf(hostId));

            return alojamientos.stream()
                    .filter(Alojamiento::getActivo) // Solo alojamientos activos
                    .map(alojamientoMapper::toDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error listando alojamientos del anfitrión {}: {}", hostId, e.getMessage());
            throw new RuntimeException("Error al listar alojamientos: " + e.getMessage());
        }
    }

    // ✅ BUSCAR ALOJAMIENTOS CON FILTROS
    @Override
    @Transactional(readOnly = true)
    public PaginacionDTO<AlojamientoDTO> buscarAlojamientos(FiltroBusquedaDTO filtro) {
        try {
            Pageable pageable = PageRequest.of(
                    filtro.pagina(),
                    filtro.tamanio(),
                    Sort.by("calificacionPromedio").descending()
            );

            Specification<Alojamiento> spec = buildSpecificationBusqueda(filtro);
            Page<Alojamiento> page = alojamientoRepository.findAll(spec, pageable);

            List<AlojamientoDTO> contenido = page.getContent()
                    .stream()
                    .map(alojamientoMapper::toDTO)
                    .collect(Collectors.toList());

            return new PaginacionDTO<>(
                    contenido,
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );

        } catch (Exception e) {
            log.error("Error buscando alojamientos: {}", e.getMessage());
            throw new RuntimeException("Error al buscar alojamientos: " + e.getMessage());
        }
    }

    // ✅ ESPECIFICACIÓN PARA BÚSQUEDA CON FILTROS
    private Specification<Alojamiento> buildSpecificationBusqueda(FiltroBusquedaDTO filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ✅ Solo alojamientos activos
            predicates.add(cb.isTrue(root.get("activo")));

            // ✅ Filtro por ciudad
            if (filtro.ciudad() != null && !filtro.ciudad().trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("ciudad")), filtro.ciudad().toLowerCase()));
            }

            // ✅ Filtro por tipo
            if (filtro.tipo() != null) {
                predicates.add(cb.equal(root.get("tipo"), filtro.tipo()));
            }

            // ✅ Filtro por precio mínimo
            if (filtro.precioMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precioPorNoche"), filtro.precioMin()));
            }

            // ✅ Filtro por precio máximo
            if (filtro.precioMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precioPorNoche"), filtro.precioMax()));
            }

            // ✅ Filtro por servicios
            if (filtro.servicios() != null && !filtro.servicios().isEmpty()) {
                for (String servicio : filtro.servicios()) {
                    predicates.add(cb.isMember(servicio, root.get("servicios")));
                }
            }

            // ✅ Búsqueda por texto (nombre, descripción, ciudad)
            if (filtro.query() != null && !filtro.query().trim().isEmpty()) {
                String searchTerm = "%" + filtro.query().toLowerCase() + "%";
                Predicate nombrePredicate = cb.like(cb.lower(root.get("nombre")), searchTerm);
                Predicate descripcionPredicate = cb.like(cb.lower(root.get("descripcion")), searchTerm);
                Predicate ciudadPredicate = cb.like(cb.lower(root.get("ciudad")), searchTerm);

                predicates.add(cb.or(nombrePredicate, descripcionPredicate, ciudadPredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ✅ VERIFICAR SI PUEDE ELIMINARSE
    @Override
    @Transactional(readOnly = true)
    public boolean puedeEliminarse(String alojamientoId) {
        try {
            Alojamiento alojamiento = alojamientoRepository.findById(Long.valueOf(alojamientoId))
                    .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

            // No puede eliminarse si tiene reservas futuras
            return !tieneReservasFuturas(alojamiento);

        } catch (Exception e) {
            log.error("Error verificando si alojamiento puede eliminarse {}: {}", alojamientoId, e.getMessage());
            return false;
        }
    }

    // ✅ MARCAR IMAGEN PRINCIPAL
    @Override
    public AlojamientoDTO marcarImagenPrincipal(String alojamientoId, String imageId) {
        try {
            Alojamiento alojamiento = alojamientoRepository.findById(Long.valueOf(alojamientoId))
                    .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

            List<String> imagenes = alojamiento.getImagenes();
            if (imagenes == null || imagenes.isEmpty()) {
                throw new IllegalArgumentException("El alojamiento no tiene imágenes");
            }

            // Verificar que la imagen existe
            if (!imagenes.contains(imageId)) {
                throw new IllegalArgumentException("La imagen no existe en este alojamiento");
            }

            // Mover la imagen al principio de la lista (posición 0 = principal)
            imagenes.remove(imageId);
            imagenes.add(0, imageId);
            alojamiento.setImagenes(imagenes);

            Alojamiento alojamientoActualizado = alojamientoRepository.save(alojamiento);
            log.info("Imagen principal actualizada para alojamiento {}: {}", alojamientoId, imageId);

            return alojamientoMapper.toDTO(alojamientoActualizado);

        } catch (IllegalArgumentException e) {
            log.error("Error marcando imagen principal: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error marcando imagen principal para alojamiento {}: {}", alojamientoId, e.getMessage());
            throw new RuntimeException("Error al marcar imagen principal: " + e.getMessage());
        }
    }

    @Override
    public List<AlojamientoDTO> obtenerTodosAlojamientos() {
//        System.out.println("🔍 Buscando todos los alojamientos activos");
//
//        // Asumiendo que tienes un repository
//        List<Alojamiento> alojamientos = alojamientoRepository.findByActivoTrue();
//
//        return alojamientos.stream()
//                .map(this::convertirToDTO)
//                .collect(Collectors.toList());
        return List.of();
    }

    @Override
    public AlojamientoDTO obtenerAlojamientoPorId(Long id) {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));
        return alojamientoMapper.toDTO(alojamiento);
    }

}