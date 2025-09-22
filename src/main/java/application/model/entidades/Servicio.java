package application.model.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "servicios") //habitaciones, baños...
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
}

