package application.repositories;

import application.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    //  Buscar comentarios activos de un alojamiento
    Page<Comentario> findByAlojamientoIdAndActivoTrue(Long alojamientoId, Pageable pageable);

    //  Buscar comentarios con respuesta
    List<Comentario> findByAlojamientoIdAndRespuestaIsNotNullAndActivoTrue(Long alojamientoId);

    //  Verificar si existe comentario para una reserva
    boolean existsByReservaId(Long reservaId);

    //  Contar comentarios activos de un alojamiento
    Integer countByAlojamientoIdAndActivoTrue(Long alojamientoId);

    //  Calcular promedio de calificación
    @Query("SELECT AVG(c.calificacion) FROM Comentario c WHERE c.alojamiento.id = :alojamientoId AND c.activo = true")
    Double calcularPromedioCalificacionByAlojamientoId(@Param("alojamientoId") Long alojamientoId);

    //  Obtener comentarios por usuario
    List<Comentario> findByUsuarioIdAndActivoTrue(Long usuarioId);
}