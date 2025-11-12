package application.repositories;

import application.model.Alojamiento;
import application.model.enums.TipoAlojamiento;
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
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, JpaSpecificationExecutor<Alojamiento> {

    List<Alojamiento> findAll();

    List<Alojamiento> findByAnfitrionId(Long anfitrionId);

    List<Alojamiento> findByActivoTrue();

    List<Alojamiento> findByTipo(TipoAlojamiento tipo);

    Page<Alojamiento> findByAnfitrionId(Long anfitrionId, Pageable pageable);

    Page<Alojamiento> findByActivoTrue(Pageable pageable);

    Page<Alojamiento> findByTipoAndActivoTrue(TipoAlojamiento tipo, Pageable pageable);

    List<Alojamiento> findByCiudadAndActivoTrue(String ciudad);

    List<Alojamiento> findByPaisAndActivoTrue(String pais);

    List<Alojamiento> findByCiudadAndTipoAndActivoTrue(String ciudad, TipoAlojamiento tipo);

    @Query("SELECT a FROM Alojamiento a WHERE a.precioPorNoche BETWEEN :precioMin AND :precioMax AND a.activo = true")
    List<Alojamiento> findByPrecioPorNocheBetween(@Param("precioMin") Double precioMin,
                                                  @Param("precioMax") Double precioMax);

    //  BÚSQUEDA POR CAPACIDAD

    List<Alojamiento> findByCapacidadMaximaGreaterThanEqualAndActivoTrue(Integer capacidadMinima);

    //  BÚSQUEDA POR SERVICIOS (usando la relación @ElementCollection)

    @Query("SELECT DISTINCT a FROM Alojamiento a JOIN a.servicios s WHERE s IN :servicios AND a.activo = true")
    List<Alojamiento> findByServiciosIn(@Param("servicios") List<String> servicios);

    //  BÚSQUEDA POR FECHAS DISPONIBLES (para el calendario interactivo)

    @Query("SELECT a FROM Alojamiento a WHERE a.id NOT IN (" +
            "SELECT r.alojamiento.id FROM Reserva r WHERE " +
            "r.estado IN (application.model.enums.EstadoReserva.PENDIENTE, application.model.enums.EstadoReserva.CONFIRMADA) AND " +
            "((r.checkIn <= :checkOut AND r.checkOut >= :checkIn))" +
            ") AND a.activo = true")
    List<Alojamiento> findAvailableAlojamientos(@Param("checkIn") LocalDate checkIn,
                                                @Param("checkOut") LocalDate checkOut);

    //  BÚSQUEDA AVANZADA CON MÚLTIPLES FILTROS
    @Query("SELECT a FROM Alojamiento a WHERE " +
            "(:ciudad IS NULL OR a.ciudad = :ciudad) AND " +
            "(:tipo IS NULL OR a.tipo = :tipo) AND " +
            "(:precioMin IS NULL OR a.precioPorNoche >= :precioMin) AND " +
            "(:precioMax IS NULL OR a.precioPorNoche <= :precioMax) AND " +
            "(:capacidadMin IS NULL OR a.capacidadMaxima >= :capacidadMin) AND " +
            "a.activo = true")
    List<Alojamiento> findByAdvancedFilters(@Param("ciudad") String ciudad,
                                            @Param("tipo") TipoAlojamiento tipo,
                                            @Param("precioMin") Double precioMin,
                                            @Param("precioMax") Double precioMax,
                                            @Param("capacidadMin") Integer capacidadMin);

    //  VERIFICAR SI UN ALOJAMIENTO ESTÁ DISPONIBLE PARA FECHAS ESPECÍFICAS

    @Query("SELECT COUNT(r) = 0 FROM Reserva r WHERE " +
            "r.alojamiento.id = :alojamientoId AND " +
            "r.estado IN (application.model.enums.EstadoReserva.PENDIENTE, application.model.enums.EstadoReserva.CONFIRMADA) AND " +
            "((r.checkIn <= :checkOut AND r.checkOut >= :checkIn))")
    boolean isAlojamientoAvailable(@Param("alojamientoId") Long alojamientoId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);
    //  ENCONTRAR ALOJAMIENTOS CON MEJORES CALIFICACIONES

    @Query("SELECT a FROM Alojamiento a WHERE a.calificacionPromedio >= :calificacionMinima AND a.activo = true ORDER BY a.calificacionPromedio DESC")
    List<Alojamiento> findTopRatedAlojamientos(@Param("calificacionMinima") Double calificacionMinima, Pageable pageable);

    // BÚSQUEDA POR TEXTO (búsqueda en nombre, descripción, ciudad)

    @Query("SELECT a FROM Alojamiento a WHERE " +
            "(LOWER(a.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.ciudad) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "a.activo = true")
    List<Alojamiento> findBySearchQuery(@Param("query") String query, Pageable pageable);

}