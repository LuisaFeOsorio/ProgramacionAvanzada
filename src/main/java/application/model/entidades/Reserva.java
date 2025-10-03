package application.model.entidades;

import application.model.enums.EstadoReserva;
import application.model.enums.Servicio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    // --- Relaciones ---

    // Un usuario puede tener muchas reservas
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Un alojamiento puede tener muchas reservas
    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;


    // Una reserva puede incluir servicios adicionales contratados
    @ElementCollection(targetClass = Servicio.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "reserva_servicios", joinColumns = @JoinColumn(name = "reserva_id"))
    @Column(name = "servicio")
    private List<Servicio> serviciosExtras = new ArrayList<>();

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Comentario comentario;


}