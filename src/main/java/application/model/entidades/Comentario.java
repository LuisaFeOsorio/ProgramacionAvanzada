package application.model.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 500, nullable = false)
    private String texto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 500)
    private String respuestaAnfitrion;

    // --- Relaciones ---

    // El usuario (huésped) que hizo el comentario
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // El alojamiento al que corresponde el comentario
    @ManyToOne(optional = false)
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    // Una reserva puede tener un comentario (solo después del check-out)
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true, nullable = false)
    private Reserva reserva;}