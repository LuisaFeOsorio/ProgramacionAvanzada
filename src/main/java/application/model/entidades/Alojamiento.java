package application.model.entidades;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "alojamientos")
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    private String ciudad;
    private String direccion;
    private Double latitud;
    private Double longitud;

    private Double precioPorNoche;
    private Integer capacidadMaxima;

    private Boolean eliminado = false;

    @ManyToOne
    @JoinColumn(name = "anfitrion_id")
    private Usuario anfitrion;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<Imagen> imagenes;

    @ManyToMany
    @JoinTable(
            name = "alojamiento_servicios",
            joinColumns = @JoinColumn(name = "alojamiento_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> servicios;
}

