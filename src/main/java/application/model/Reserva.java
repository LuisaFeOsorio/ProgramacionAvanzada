package application.model;

import application.model.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private Integer numeroHuespedes;

    @Column(name = "precio_total", nullable = false)
    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // --- Relaciones ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    // ✅ CAMBIO: Cambiar de Enum a String para más flexibilidad
    @ElementCollection
    @CollectionTable(name = "reserva_servicios", joinColumns = @JoinColumn(name = "reserva_id"))
    @Column(name = "servicio")
    private List<String> serviciosExtras = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comentario> comentarios = new ArrayList<>();

    // --- Métodos del ciclo de vida ---

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoReserva.PENDIENTE;
        }
    }

    // --- Métodos de negocio ---

    public void agregarServicioExtra(String servicio) {
        if (this.serviciosExtras == null) {
            this.serviciosExtras = new ArrayList<>();
        }
        this.serviciosExtras.add(servicio);
    }

    public void removerServicioExtra(String servicio) {
        if (this.serviciosExtras != null) {
            this.serviciosExtras.remove(servicio);
        }
    }

    public boolean puedeCancelarse() {
        return this.estado == EstadoReserva.PENDIENTE ||
                this.estado == EstadoReserva.CONFIRMADA;
    }

    public boolean estaActiva() {
        LocalDate hoy = LocalDate.now();
        return !checkOut.isBefore(hoy) &&
                (estado == EstadoReserva.CONFIRMADA);
    }

    public long calcularDias() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    // --- Métodos toString, equals, hashCode ---

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", numeroHuespedes=" + numeroHuespedes +
                ", precioTotal=" + precioTotal +
                ", estado=" + estado +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioId=" + (usuario != null ? usuario.getId() : "null") +
                ", alojamientoId=" + (alojamiento != null ? alojamiento.getId() : "null") +
                '}';
    }
}