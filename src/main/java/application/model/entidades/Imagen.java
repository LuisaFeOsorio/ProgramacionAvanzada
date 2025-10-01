package application.model.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "imagenes")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private Boolean principal = false;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;
}
