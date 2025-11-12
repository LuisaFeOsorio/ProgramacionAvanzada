package application.repositories;

import application.model.Reserva;
import application.model.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    // ✅ VALIDAR DISPONIBILIDAD - CORREGIDO
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r WHERE " +
            "r.alojamiento.id = :alojamientoId AND " +
            "r.estado IN (:estadosValidos) AND " +
            "((r.checkIn < :checkOut AND r.checkOut > :checkIn))")
    boolean existsByAlojamientoIdAndFechasSolapadas(
            @Param("alojamientoId") Long alojamientoId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("estadosValidos") List<EstadoReserva> estadosValidos
    );
    // En ReservaRepository.java
    List<Reserva> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // ✅ PARA CALENDARIO DEL ANFITRIÓN - CORREGIDO
    @Query("SELECT r FROM Reserva r WHERE " +
            "r.alojamiento.id = :alojamientoId AND " +
            "r.estado IN (:estados) AND " +
            "((r.checkIn BETWEEN :desde AND :hasta) OR " +
            "(r.checkOut BETWEEN :desde AND :hasta) OR " +
            "(r.checkIn <= :desde AND r.checkOut >= :hasta))")
    List<Reserva> findByAlojamientoIdAndFechas(
            @Param("alojamientoId") Long alojamientoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("estados") List<EstadoReserva> estados
    );

    Page<Reserva> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Reserva> findByUsuarioIdAndEstado(Long usuarioId, EstadoReserva estado, Pageable pageable);

    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId ORDER BY r.fechaCreacion DESC")
    Page<Reserva> findByAlojamientoAnfitrionId(@Param("anfitrionId") Long anfitrionId, Pageable pageable);

    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId AND r.estado = :estado ORDER BY r.fechaCreacion DESC")
    Page<Reserva> findByAlojamientoAnfitrionIdAndEstado(
            @Param("anfitrionId") Long anfitrionId,
            @Param("estado") EstadoReserva estado,
            Pageable pageable
    );

    @Query("SELECT r FROM Reserva r WHERE r.checkOut < :hoy AND r.estado IN (:estados)")
    List<Reserva> findByCheckOutBeforeAndEstadoIn(
            @Param("hoy") LocalDate hoy,
            @Param("estados") List<EstadoReserva> estados
    );

    @Query("SELECT r FROM Reserva r WHERE r.checkIn = :fecha AND r.estado = :estado ORDER BY r.fechaCreacion DESC")
    List<Reserva> findByCheckInAndEstado(
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoReserva estado
    );

    long countByEstado(EstadoReserva estado);

    // ✅ CONTAR RESERVAS POR USUARIO Y ESTADO
    long countByUsuarioIdAndEstado(Long usuarioId, EstadoReserva estado);

    // ✅ CONTAR RESERVAS POR ANFITRIÓN Y ESTADO - CORREGIDO
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId AND r.estado = :estado")
    long countByAnfitrionIdAndEstado(@Param("anfitrionId") Long anfitrionId, @Param("estado") EstadoReserva estado);

    // ✅ RESERVAS QUE REQUIEREN ATENCIÓN (PENDIENTES DE ANFITRIÓN) - CORREGIDO
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId AND r.estado = 'PENDIENTE' ORDER BY r.fechaCreacion DESC")
    List<Reserva> findReservasPendientesPorAnfitrion(@Param("anfitrionId") Long anfitrionId);

    // ✅ MÉTODO PARA VALIDAR SI UNA RESERVA PERTENECE A UN USUARIO
    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

    // ✅ NUEVO: Encontrar reserva por ID con usuario y alojamiento (para optimización)
    @Query("SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.alojamiento WHERE r.id = :id")
    Optional<Reserva> findByIdWithUsuarioAndAlojamiento(@Param("id") Long id);

    // ✅ NUEVO: Reservas activas de un usuario (para dashboard)
    @Query("SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId AND " +
            "r.estado IN ('CONFIRMADA', 'EN_CURSO', 'PENDIENTE') AND " +
            "r.checkOut >= :hoy ORDER BY r.checkIn ASC")
    List<Reserva> findReservasActivasByUsuario(@Param("usuarioId") Long usuarioId, @Param("hoy") LocalDate hoy);

    // ✅ NUEVO: Reservas futuras de un alojamiento
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.id = :alojamientoId AND " +
            "r.checkIn >= :hoy AND r.estado IN ('CONFIRMADA', 'PENDIENTE') " +
            "ORDER BY r.checkIn ASC")
    List<Reserva> findReservasFuturasByAlojamiento(@Param("alojamientoId") Long alojamientoId, @Param("hoy") LocalDate hoy);

    // ✅ NUEVO: Verificar si usuario tiene reserva en alojamiento específico (para comentarios)
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE " +
            "r.usuario.id = :usuarioId AND " +
            "r.alojamiento.id = :alojamientoId AND " +
            "r.estado IN ('COMPLETADA', 'EN_CURSO')")
    boolean existsByUsuarioIdAndAlojamientoIdAndEstadoValido(
            @Param("usuarioId") Long usuarioId,
            @Param("alojamientoId") Long alojamientoId
    );
}