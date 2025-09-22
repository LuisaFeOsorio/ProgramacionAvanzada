package application.model.entidades;

import application.model.Role;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String contraseña;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private Role rol;

    private LocalDate fechaNacimiento;

    private String fotoPerfil;

    private String descripcionPersonal;

    private Boolean activo = true;

    @OneToMany(mappedBy = "anfitrion", cascade = CascadeType.ALL)
    private List<Alojamiento> alojamientos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;
}
