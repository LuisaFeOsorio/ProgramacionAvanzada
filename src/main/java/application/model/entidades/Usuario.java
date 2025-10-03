package application.model.entidades;

import application.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter

@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nombre;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String contrasenia;

    @Column(length = 50)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Role rol;

    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fotoPerfil", length = 300)
    private String fotoPerfil;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @OneToMany(mappedBy = "anfitrion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alojamiento> alojamientos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();}