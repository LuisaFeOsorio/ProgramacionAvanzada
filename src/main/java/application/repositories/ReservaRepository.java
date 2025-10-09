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

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    // ✅ VALIDAR DISPONIBILIDAD (CORREGIDO)
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE " +
            "r.alojamiento.id = :alojamientoId AND " +
            "r.estado IN :estados AND " +
            "((r.checkIn < :checkOut AND r.checkOut > :checkIn))") // CORREGIDO: < y > en lugar de <= y >=
    boolean existsByAlojamientoIdAndFechasSolapadas(
            @Param("alojamientoId") Long alojamientoId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("estados") List<EstadoReserva> estados
    );

    // ✅ PARA CALENDARIO DEL ANFITRIÓN (MEJORADO)
    @Query("SELECT r FROM Reserva r WHERE " +
            "r.alojamiento.id = :alojamientoId AND " +
            "r.estado IN :estados AND " +
            "((r.checkIn BETWEEN :desde AND :hasta) OR " +
            "(r.checkOut BETWEEN :desde AND :hasta) OR " +
            "(r.checkIn <= :desde AND r.checkOut >= :hasta) OR " +
            "(r.checkIn >= :desde AND r.checkOut <= :hasta))")
    List<Reserva> findByAlojamientoIdAndFechas(
            @Param("alojamientoId") Long alojamientoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("estados") List<EstadoReserva> estados
    );

    // ✅ RESERVAS POR USUARIO (PAGINADO)
    Page<Reserva> findByUsuarioId(Long usuarioId, Pageable pageable);

    // ✅ RESERVAS POR USUARIO Y ESTADO (ÚTIL PARA FILTROS)
    Page<Reserva> findByUsuarioIdAndEstado(Long usuarioId, EstadoReserva estado, Pageable pageable);

    // ✅ RESERVAS POR USUARIO (LISTA SIMPLE)
    List<Reserva> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // ✅ RESERVAS POR ANFITRIÓN (PAGINADO)
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId")
    Page<Reserva> findByAlojamientoAnfitrionId(@Param("anfitrionId") Long anfitrionId, Pageable pageable);

    // ✅ RESERVAS POR ANFITRIÓN Y ESTADO (ÚTIL PARA FILTROS)
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId AND r.estado = :estado")
    Page<Reserva> findByAlojamientoAnfitrionIdAndEstado(
            @Param("anfitrionId") Long anfitrionId,
            @Param("estado") EstadoReserva estado,
            Pageable pageable
    );

    // ✅ RESERVAS POR ALOJAMIENTO (PAGINADO)
    Page<Reserva> findByAlojamientoId(Long alojamientoId, Pageable pageable);

    // ✅ RESERVAS POR ALOJAMIENTO (LISTA SIMPLE)
    List<Reserva> findByAlojamientoIdOrderByCheckInDesc(Long alojamientoId);

    // ✅ RESERVAS POR ALOJAMIENTO Y ESTADO
    List<Reserva> findByAlojamientoIdAndEstadoOrderByCheckInDesc(Long alojamientoId, EstadoReserva estado);

    // ✅ ACTUALIZACIÓN AUTOMÁTICA DE ESTADOS - RESERVAS COMPLETADAS
    @Query("SELECT r FROM Reserva r WHERE r.checkOut < :hoy AND r.estado IN :estados")
    List<Reserva> findByCheckOutBeforeAndEstadoIn(
            @Param("hoy") LocalDate hoy,
            @Param("estados") List<EstadoReserva> estados
    );

    // ✅ RESERVAS PRÓXIMAS AL CHECK-IN (PARA RECORDATORIOS)
    @Query("SELECT r FROM Reserva r WHERE r.checkIn = :fecha AND r.estado = :estado")
    List<Reserva> findByCheckInAndEstado(
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoReserva estado
    );

    // ✅ CONTAR RESERVAS POR ESTADO (PARA DASHBOARD)
    long countByEstado(EstadoReserva estado);

    // ✅ CONTAR RESERVAS POR USUARIO Y ESTADO
    long countByUsuarioIdAndEstado(Long usuarioId, EstadoReserva estado);

    // ✅ CONTAR RESERVAS POR ANFITRIÓN Y ESTADO
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId AND r.estado = :estado")
    long countByAnfitrionIdAndEstado(@Param("anfitrionId") Long anfitrionId, @Param("estado") EstadoReserva estado);

    // ✅ RESERVAS QUE REQUIEREN ATENCIÓN (PENDIENTES DE ANFITRIÓN)
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.anfitrion.id = :anfitrionId AND r.estado = 'PENDIENTE'")
    List<Reserva> findReservasPendientesPorAnfitrion(@Param("anfitrionId") Long anfitrionId);
}