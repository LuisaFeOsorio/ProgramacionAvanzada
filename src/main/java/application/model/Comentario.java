package application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ CALIFICACIÓN (1-5 estrellas)
    @Column(nullable = false)
    @jakarta.validation.constraints.Min(value = 1, message = "La calificación mínima es 1")
    @jakarta.validation.constraints.Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    // ✅ CONTENIDO DEL COMENTARIO (mejor nombre que "texto")
    @Column(length = 1000, nullable = false) // Aumentado a 1000 caracteres
    @jakarta.validation.constraints.NotBlank(message = "El contenido del comentario es requerido")
    @jakarta.validation.constraints.Size(min = 10, max = 1000, message = "El comentario debe tener entre 10 y 1000 caracteres")
    private String contenido;

    // ✅ RESPUESTA DEL ANFITRIÓN
    @Column(length = 1000) // Aumentado a 1000 caracteres
    private String respuesta;

    // ✅ FECHAS (mejor usar LocalDateTime)
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    // ✅ ESTADO ACTIVO (para borrado lógico)
    @Column(nullable = false)
    private Boolean activo = true;

    // --- RELACIONES ---

    // ✅ USUARIO (HUÉSPED) QUE HIZO EL COMENTARIO
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ✅ ALOJAMIENTO AL QUE CORRESPONDE EL COMENTARIO
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    // ✅ RESERVA ASOCIADA (relación corregida)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    // ✅ CONSTRUCTORES
    public Comentario() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    public Comentario(Integer calificacion, String contenido, Usuario usuario, Alojamiento alojamiento, Reserva reserva) {
        this();
        this.calificacion = calificacion;
        this.contenido = contenido;
        this.usuario = usuario;
        this.alojamiento = alojamiento;
        this.reserva = reserva;
    }

    // ✅ MÉTODOS DE CONVENIENCIA
    public boolean tieneRespuesta() {
        return respuesta != null && !respuesta.trim().isEmpty();
    }

    public boolean esReciente() {
        return fechaCreacion != null &&
                fechaCreacion.isAfter(LocalDateTime.now().minusDays(30));
    }

    public boolean esPositivo() {
        return calificacion != null && calificacion >= 4;
    }

    public boolean esNegativo() {
        return calificacion != null && calificacion <= 2;
    }

    public String getCalificacionEstrellas() {
        if (calificacion == null) return "Sin calificación";
        return "★".repeat(calificacion) + "☆".repeat(5 - calificacion);
    }

    public void agregarRespuesta(String respuesta) {
        this.respuesta = respuesta;
        this.fechaRespuesta = LocalDateTime.now();
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    // ✅ MÉTODO PARA VALIDAR ANTES DE PERSISTIR
    @PrePersist
    @PreUpdate
    public void validar() {
        if (calificacion == null || calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        if (contenido == null || contenido.trim().length() < 10) {
            throw new IllegalArgumentException("El comentario debe tener al menos 10 caracteres");
        }
        if (contenido.trim().length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede exceder 1000 caracteres");
        }
        if (respuesta != null && respuesta.trim().length() > 1000) {
            throw new IllegalArgumentException("La respuesta no puede exceder 1000 caracteres");
        }
    }

    // ✅ MÉTODO toString MEJORADO
    @Override
    public String toString() {
        return "Comentario{" +
                "id=" + id +
                ", calificacion=" + calificacion +
                ", contenido='" + (contenido != null ? contenido.substring(0, Math.min(50, contenido.length())) + "..." : "null") + '\'' +
                ", tieneRespuesta=" + tieneRespuesta() +
                ", activo=" + activo +
                ", usuarioId=" + (usuario != null ? usuario.getId() : "null") +
                ", alojamientoId=" + (alojamiento != null ? alojamiento.getId() : "null") +
                '}';
    }
}