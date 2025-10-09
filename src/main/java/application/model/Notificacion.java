package application.model;

import application.model.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacion tipo;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // ✅ CONSTRUCTORES
    public Notificacion() {}

    public Notificacion(String titulo, String mensaje, TipoNotificacion tipo, Usuario usuario) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.usuario = usuario;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
    }
}