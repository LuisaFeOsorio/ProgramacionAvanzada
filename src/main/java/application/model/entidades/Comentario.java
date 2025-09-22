package application.model.entidades;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "comentarios")
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer calificacion;

    @Column(length = 500)
    private String texto;

    private LocalDate fecha;

    private String respuestaAnfitrion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;

    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;
}
