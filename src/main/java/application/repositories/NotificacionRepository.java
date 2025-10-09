package application.repositories;

import application.model.Notificacion;
import application.model.enums.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    //  CONSULTAS BÁSICAS (sin @Query)
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(Long usuarioId);

    Long countByUsuarioIdAndLeidaFalse(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndTipoOrderByFechaCreacionDesc(Long usuarioId, TipoNotificacion tipo);

    //  MARCAR COMO LEÍDA - Con método personalizado
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id = :id AND n.usuario.id = :usuarioId")
    int marcarComoLeida(@Param("id") Long id, @Param("usuarioId") Long usuarioId);

    //  MARCAR TODAS COMO LEÍDAS
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.id = :usuarioId AND n.leida = false")
    int marcarTodasComoLeidas(@Param("usuarioId") Long usuarioId);

    //  ELIMINAR NOTIFICACIONES ANTIGUAS LEÍDAS
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leida = true AND n.fechaCreacion < :fechaLimite")
    int eliminarNotificacionesAntiguas(@Param("usuarioId") Long usuarioId, @Param("fechaLimite") LocalDateTime fechaLimite);
}