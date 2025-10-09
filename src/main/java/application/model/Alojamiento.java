package application.model;

import application.model.enums.TipoAlojamiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAlojamiento tipo;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 100)
    private String pais;

    @Column(nullable = false, length = 500)
    private String direccion;

    @Column(name = "precio_por_noche", nullable = false)
    private Double precioPorNoche;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "numero_habitaciones")
    private Integer numeroHabitaciones;

    @Column(name = "numero_banos")
    private Integer numeroBanos;

    @ElementCollection
    @CollectionTable(name = "alojamiento_servicios", joinColumns = @JoinColumn(name = "alojamiento_id"))
    @Column(name = "servicio")
    private List<String> servicios = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "alojamiento_imagenes", joinColumns = @JoinColumn(name = "alojamiento_id"))
    @Column(name = "url_imagen")
    private List<String> imagenes = new ArrayList<>();

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    @Column(name = "total_calificaciones")
    private Integer totalCalificaciones = 0;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // --- RELACIONES ---

    @ManyToOne
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private Usuario anfitrion;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

}