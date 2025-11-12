package application.services.impl;

import application.dto.paginacion.PaginacionDTO;
import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.FiltroReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.exceptions.reserva.ReservaNoCanceladaException;
import application.exceptions.reserva.ReservasNoObtenidasException;
import application.mappers.ReservaMapper;
import application.model.Alojamiento;
import application.model.Reserva;
import application.model.Usuario;
import application.model.enums.EstadoReserva;
import application.repositories.AlojamientoRepository;
import application.repositories.ReservaRepository;
import application.repositories.UsuarioRepository;
import application.services.NotificacionesService;
import application.services.email.EmailService;
import application.services.reserva.ReservaService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ReservaMapper reservaMapping;
    private final EmailService emailService;
    private final ReservaMapper reservaMapper;
    private final NotificacionesService notificacionesService;


    // ReservaService.java - ACTUALIZADO
    @Override
    public ReservaDTO crearReserva(CrearReservaDTO dto) {
        System.out.println("🔍 === RESERVA SERVICE - INICIO ===");

        // Verificar que el alojamiento existe
        Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar fechas
        if (dto.checkIn().isAfter(dto.checkOut()) || dto.checkIn().isEqual(dto.checkOut())) {
            throw new RuntimeException("La fecha de check-in debe ser anterior al check-out");
        }

        // Validar capacidad
        if (dto.numeroHuespedes() > alojamiento.getCapacidadMaxima()) {
            throw new RuntimeException("El número de huéspedes excede la capacidad del alojamiento");
        }
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dto.checkIn(), dto.checkOut());
        double precioTotal = dias * alojamiento.getPrecioPorNoche();

        Reserva reserva = new Reserva();
        reserva.setCheckIn(dto.checkIn());
        reserva.setCheckOut(dto.checkOut());
        reserva.setNumeroHuespedes(dto.numeroHuespedes());
        reserva.setPrecioTotal(precioTotal);
        reserva.setEstado(EstadoReserva.valueOf("PENDIENTE"));
        reserva.setAlojamiento(alojamiento);
        reserva.setUsuario(usuario);
        reserva.setServiciosExtras(dto.serviciosExtras());
        reserva.setFechaCreacion(LocalDateTime.now());

        Reserva reservaGuardada = reservaRepository.save(reserva);

        System.out.println("✅ Reserva creada exitosamente con ID: " + reservaGuardada.getId());

        return reservaMapper.toDTO(reservaGuardada);
    }

    @Override
    public List<ReservaDTO> findByUsuarioId(Long usuarioId) {
        System.out.println("🔍 Buscando reservas para usuario ID: " + usuarioId);

        List<Reserva> reservas = reservaRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        System.out.println("📦 Reservas encontradas en BD: " + reservas.size());

        return reservas.stream()
                .map(reservaMapper::toDTO)
                .collect(Collectors.toList());
    }

    // VALIDACIONES MEJORADAS
    private void validarFechasReserva(LocalDate checkIn, LocalDate checkOut) {
        // Mínimo 1 noche
        if (checkIn.isAfter(checkOut) || checkIn.equals(checkOut)) {
            throw new IllegalArgumentException("El check-out debe ser posterior al check-in (mínimo 1 noche)");
        }

        // Máximo 30 días
        long noches = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (noches > 30) {
            throw new IllegalArgumentException("No se pueden reservar más de 30 noches");
        }
    }

    private void validarDisponibilidad(Long alojamientoId, LocalDate checkIn, LocalDate checkOut) {
        boolean existeSolapamiento = reservaRepository.existsByAlojamientoIdAndFechasSolapadas(
                alojamientoId, checkIn, checkOut,
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)
        );

        if (existeSolapamiento) {
            throw new IllegalArgumentException("El alojamiento no está disponible para las fechas seleccionadas");
        }
    }

    private void validarCapacidad(Alojamiento alojamiento, Integer numeroHuespedes) {
        if (numeroHuespedes > alojamiento.getCapacidadMaxima()) {
            throw new IllegalArgumentException(
                    "Número de huéspedes (" + numeroHuespedes + ") excede la capacidad máxima (" +
                            alojamiento.getCapacidadMaxima() + ")"
            );
        }

        if (numeroHuespedes < 1) {
            throw new IllegalArgumentException("Debe haber al menos 1 huésped");
        }
    }

    // ✅ CORREGIDO: Validar ambas fechas (check-in y check-out)
    private void validarFechasFuturas(LocalDate checkIn, LocalDate checkOut) {
        LocalDate hoy = LocalDate.now();

        if (checkIn.isBefore(hoy)) {
            throw new IllegalArgumentException("No se pueden reservar fechas pasadas para el check-in");
        }

        if (checkOut.isBefore(hoy)) {
            throw new IllegalArgumentException("No se pueden reservar fechas pasadas para el check-out");
        }
    }

    // ✅ NOTIFICACIONES MEJORADAS
    private void enviarNotificacionesReservaCreada(Reserva reserva) {
        try {
            // ✅ NOTIFICAR AL USUARIO - Confirmación de reserva con detalles
            emailService.enviarConfirmacionReservaUsuario(reserva);

            // ✅ NOTIFICAR AL ANFITRIÓN - Nueva reserva recibida
            emailService.enviarNotificacionNuevaReservaAnfitrion(reserva);

            // ✅ NOTIFICACIÓN EN APP PARA ANFITRIÓN
            notificacionesService.crearNotificacionAnfitrion(
                    reserva.getAlojamiento().getAnfitrion().getId(),
                    "Nueva Reserva Recibida",
                    "Tienes una nueva reserva para " + reserva.getAlojamiento().getNombre()

            );

        } catch (Exception e) {
            log.error("Error enviando notificaciones de reserva creada: {}", e.getMessage());
        }
    }

    private void enviarNotificacionesReservaCancelada(Reserva reserva, Usuario quienCancela) {
        try {
            if (quienCancela.getId().equals(reserva.getUsuario().getId())) {
                // ✅ USUARIO canceló → notificar ANFITRIÓN
                emailService.enviarNotificacionCancelacionAnfitrion(reserva);
                notificacionesService.crearNotificacionAnfitrion(
                        reserva.getAlojamiento().getAnfitrion().getId(),
                        "Reserva Cancelada por Huésped",
                        "El huésped " + reserva.getUsuario().getNombre() + " canceló su reserva"

                );
            } else {
                // ✅ ANFITRIÓN canceló → notificar USUARIO
                emailService.enviarNotificacionCancelacionUsuario(reserva);
                notificacionesService.enviarNotificacionUsuario(
                        reserva.getUsuario().getId().toString(),
                        "Reserva Cancelada por Anfitrión",
                        "El anfitrión canceló tu reserva en " + reserva.getAlojamiento().getNombre()
                );
            }
        } catch (Exception e) {
            log.error("Error enviando notificaciones de cancelación: {}", e.getMessage());
        }
    }

    // ✅ OBTENER RESERVA
    @Override
    @Transactional(readOnly = true)
    public ReservaDTO obtenerReserva(String reservaId) throws ReservasNoObtenidasException {
        Reserva reserva = reservaRepository.findById(Long.valueOf(reservaId))
                .orElseThrow(() -> new ReservasNoObtenidasException("Reserva no encontrada"));
        return reservaMapping.toDTO(reserva);
    }

    // ✅ CANCELACIÓN DE RESERVA MEJORADA
    @Override
    public void cancelarReserva(String reservaId, String quienCancelaId) throws ReservaNoCanceladaException {
        try {
            Reserva reserva = reservaRepository.findById(Long.valueOf(reservaId))
                    .orElseThrow(() -> new ReservaNoCanceladaException("Reserva no encontrada"));

            Usuario quienCancela = usuarioRepository.findById(Long.valueOf(quienCancelaId))
                    .orElseThrow(() -> new ReservaNoCanceladaException("Usuario no encontrado"));

            // Validar que puede cancelar
            validarCancelacion(reserva, quienCancela);

            // Cambiar estado
            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaRepository.save(reserva);

            // Notificar cancelación
            enviarNotificacionesReservaCancelada(reserva, quienCancela);

        } catch (IllegalArgumentException e) {
            throw new ReservaNoCanceladaException(e.getMessage());
        } catch (Exception e) {
            log.error("Error cancelando reserva: {}", e.getMessage(), e);
            throw new ReservaNoCanceladaException("Error al cancelar reserva: " + e.getMessage());
        }
    }

    // ✅ VALIDACIÓN DE CANCELACIÓN MEJORADA
    private void validarCancelacion(Reserva reserva, Usuario quienCancela) {
        // Solo usuario o anfitrión pueden cancelar
        boolean esUsuario = reserva.getUsuario().getId().equals(quienCancela.getId());
        boolean esAnfitrion = reserva.getAlojamiento().getAnfitrion().getId().equals(quienCancela.getId());

        if (!esUsuario && !esAnfitrion) {
            throw new IllegalArgumentException("No tienes permisos para cancelar esta reserva");
        }

        // ✅ CORREGIDO: Si es usuario, validar 48 horas antes (exacto)
        if (esUsuario) {
            LocalDateTime limiteCancelacion = reserva.getCheckIn().atStartOfDay().minusHours(48);
            if (LocalDateTime.now().isAfter(limiteCancelacion)) {
                throw new IllegalArgumentException(
                        "Solo se puede cancelar hasta 48 horas antes del check-in. " +
                                "Límite: " + limiteCancelacion
                );
            }
        }

        // Validar estado
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException("La reserva ya está cancelada");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new IllegalArgumentException("No se puede cancelar una reserva completada");
        }

        if (reserva.getEstado() == EstadoReserva.RECHAZADA) {
            throw new IllegalArgumentException("No se puede cancelar una reserva rechazada");
        }
    }

    // ✅ APROBAR RESERVA (ANFITRIÓN)
    @Override
    public ReservaDTO aprobarReserva(String reservaId, String anfitrionId) {
        Reserva reserva = reservaRepository.findById(Long.valueOf(reservaId))
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Validar que el anfitrión es dueño del alojamiento
        if (!reserva.getAlojamiento().getAnfitrion().getId().equals(Long.valueOf(anfitrionId))) {
            throw new IllegalArgumentException("No tienes permisos para aprobar esta reserva");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);

        // ✅ NOTIFICAR APROBACIÓN
        try {
            emailService.enviarNotificacionAprobacionReserva(reservaActualizada);
            notificacionesService.enviarNotificacionUsuario(
                    reserva.getUsuario().getId().toString(),
                    "Reserva Aprobada",
                    "Tu reserva en " + reserva.getAlojamiento().getNombre() + " ha sido aprobada"

            );
        } catch (Exception e) {
            log.error("Error notificando aprobación de reserva: {}", e.getMessage());
        }

        return reservaMapping.toDTO(reservaActualizada);
    }

    // ✅ RECHAZAR RESERVA (ANFITRIÓN)
    @Override
    public ReservaDTO rechazarReserva(String reservaId, String anfitrionId) {
        Reserva reserva = reservaRepository.findById(Long.valueOf(reservaId))
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Validar que el anfitrión es dueño del alojamiento
        if (!reserva.getAlojamiento().getAnfitrion().getId().equals(Long.valueOf(anfitrionId))) {
            throw new IllegalArgumentException("No tienes permisos para rechazar esta reserva");
        }

        reserva.setEstado(EstadoReserva.RECHAZADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);

        // ✅ NOTIFICAR RECHAZO
        try {
            emailService.enviarNotificacionRechazoReserva(reservaActualizada);
            notificacionesService.enviarNotificacionUsuario(
                    reserva.getUsuario().getId().toString(),
                    "Reserva Rechazada",
                    "Tu reserva en " + reserva.getAlojamiento().getNombre() + " fue rechazada"
            );
        } catch (Exception e) {
            log.error("Error notificando rechazo de reserva: {}", e.getMessage());
        }

        return reservaMapping.toDTO(reservaActualizada);
    }

    // ✅ LISTADO DE RESERVAS CON FILTROS MEJORADO
    @Override
    @Transactional(readOnly = true)
    public PaginacionDTO<ReservaDTO> listarReservas(FiltroReservaDTO filtro) {
        Pageable pageable = PageRequest.of(
                filtro.pagina(),
                filtro.tamanio(),
                Sort.by(Sort.Direction.DESC, "fechaCreacion") // ✅ SIEMPRE más reciente primero
        );

        Specification<Reserva> spec = buildSpecification(filtro);
        Page<Reserva> page = reservaRepository.findAll(spec, pageable);

        List<ReservaDTO> contenido = page.getContent()
                .stream()
                .map(reservaMapping::toDTO)
                .collect(Collectors.toList());

        return new PaginacionDTO<>(
                contenido,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<Reserva> buildSpecification(FiltroReservaDTO filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ✅ FILTRO POR ALOJAMIENTO
            if (filtro.alojamientoId() != null) {
                predicates.add(cb.equal(root.get("alojamiento").get("id"), filtro.alojamientoId()));
            }

            // ✅ FILTRO POR USUARIO
            if (filtro.usuarioId() != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), Long.valueOf(filtro.usuarioId())));
            }

            // ✅ FILTRO POR ANFITRIÓN (NUEVO)
            if (filtro.anfitrionId() != null) {
                predicates.add(cb.equal(root.get("alojamiento").get("anfitrion").get("id"), Long.valueOf(filtro.anfitrionId())));
            }

            // ✅ FILTRO POR ESTADO
            if (filtro.estado() != null && !filtro.estado().trim().isEmpty()) {
                try {
                    EstadoReserva estadoEnum = EstadoReserva.valueOf(filtro.estado().toUpperCase());
                    predicates.add(cb.equal(root.get("estado"), estadoEnum));
                } catch (IllegalArgumentException e) {
                    log.warn("Estado de reserva no válido: {}", filtro.estado());
                }
            }

            // ✅ FILTRO POR FECHA INICIO (checkIn >= fechaInicio)
            if (filtro.fechaInicio() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkIn"), filtro.fechaInicio()));
            }

            // ✅ FILTRO POR FECHA FIN (checkOut <= fechaFin)
            if (filtro.fechaFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkOut"), filtro.fechaFin()));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ✅ RESERVAS POR ALOJAMIENTO (PARA CALENDARIO) - MEJORADO
    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> reservasParaCalendario(String alojamientoId, LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findByAlojamientoIdAndFechas(
                Long.valueOf(alojamientoId),
                desde != null ? desde : LocalDate.now().minusMonths(1),
                hasta != null ? hasta : LocalDate.now().plusMonths(6),
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)
        );

        return reservas.stream()
                .map(reservaMapping::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ NUEVO: ACTUALIZAR AUTOMÁTICAMENTE ESTADOS (COMPLETADAS)
    @Scheduled(cron = "0 0 2 * * ?") // Ejecutar diariamente a las 2 AM
    @Transactional
    public void actualizarEstadosReservas() {
        LocalDate hoy = LocalDate.now();

        // Marcar reservas como COMPLETADAS (check-out pasado)
        List<Reserva> reservasCompletadas = reservaRepository.findByCheckOutBeforeAndEstadoIn(
                hoy, List.of(EstadoReserva.CONFIRMADA)
        );

        for (Reserva reserva : reservasCompletadas) {
            reserva.setEstado(EstadoReserva.COMPLETADA);
        }

        reservaRepository.saveAll(reservasCompletadas);

        if (!reservasCompletadas.isEmpty()) {
            log.info("Actualizadas {} reservas a estado COMPLETADA", reservasCompletadas.size());
        }
    }

    // ✅ NUEVO: OBTENER RESERVAS POR USUARIO (ordenadas por fecha más reciente)
    @Override
    @Transactional(readOnly = true)
    public PaginacionDTO<ReservaDTO> reservasPorUsuario(String usuarioId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        Page<Reserva> page = reservaRepository.findByUsuarioId(Long.valueOf(usuarioId), pageable);

        List<ReservaDTO> contenido = page.getContent()
                .stream()
                .map(reservaMapping::toDTO)
                .collect(Collectors.toList());

        return new PaginacionDTO<>(
                contenido,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    // ✅ NUEVO: OBTENER RESERVAS POR ANFITRIÓN (ordenadas por fecha más reciente)
    @Override
    @Transactional(readOnly = true)
    public PaginacionDTO<ReservaDTO> reservasPorAnfitrion(String anfitrionId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        Page<Reserva> page = reservaRepository.findByAlojamientoAnfitrionId(Long.valueOf(anfitrionId), pageable);

        List<ReservaDTO> contenido = page.getContent()
                .stream()
                .map(reservaMapping::toDTO)
                .collect(Collectors.toList());

        return new PaginacionDTO<>(
                contenido,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public List<ReservaDTO> reservasPorAlojamiento(String alojamientoId, LocalDate desde, LocalDate hasta) {
        return List.of();
    }
}