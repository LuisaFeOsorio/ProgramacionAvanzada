package application.model.entidades;

import application.model.enumm.Servicio;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(length = 50)
    private String ciudad;

    @Column(length = 100)
    private String direccion;

    @Column(length = 50)
    private Double latitud;

    @Column(length = 50)
    private Double longitud;

    @Column(length = 50)
    private Double precioPorNoche;

    @Column(length = 50)
    private Integer capacidadMaxima;

    @Column(length = 50)
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

    @ElementCollection(targetClass = Servicio.class)
    @Enumerated(EnumType.STRING) // guarda el nombre del enum (ej: WIFI, PISCINA)
    @CollectionTable(
            name = "alojamiento_servicios",
            joinColumns = @JoinColumn(name = "alojamiento_id")
    )
    @Column(name = "servicio")
    private List<Servicio> servicios = new ArrayList<>();

}

